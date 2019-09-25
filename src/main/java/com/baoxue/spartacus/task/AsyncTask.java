package com.baoxue.spartacus.task;

import com.baoxue.spartacus.config.BlogBeanConfig;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.controller.sse.MessageBody;
import com.baoxue.spartacus.controller.sse.NewOrderNotifyEvent;
import com.baoxue.spartacus.controller.sse.NewOrderNotifyEventPublisherAware;
import com.baoxue.spartacus.globals.Globals;
import com.baoxue.spartacus.pojo.CosResource;
import com.baoxue.spartacus.controller.websocket.WbOutMessage;
import com.baoxue.spartacus.pojo.WordFrequency;
import com.baoxue.spartacus.repository.CosResourceRepository;
import com.baoxue.spartacus.repository.ElasticsearchHelper;
import com.baoxue.spartacus.service.CosService;
import com.baoxue.spartacus.service.ElasticsearchService;
import com.baoxue.spartacus.service.WebSocketService;
import com.baoxue.spartacus.utils.BeanUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.exception.MultiObjectDeleteException;
import com.qcloud.cos.exception.MultiObjectDeleteException.DeleteError;
import com.qcloud.cos.model.CopyObjectResult;
import com.qcloud.cos.model.DeleteObjectsRequest;
import com.qcloud.cos.model.DeleteObjectsRequest.KeyVersion;
import com.qcloud.cos.model.DeleteObjectsResult;
import com.qcloud.cos.model.DeleteObjectsResult.DeletedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月15日下午6:38:34
 */
@Component
public class AsyncTask {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Autowired
	ElasticsearchService elasticsearchService;
	
	@Autowired
	CosService cosService;

	@Autowired
	BlogBeanConfig blogBeanConfig;

	@Autowired
	private BlogProperties blogProperties;

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Autowired
	private CosResourceRepository cosResourceRepository;

	@Autowired
	private ElasticsearchHelper elasticsearchHelper;

	@Autowired
	private NewOrderNotifyEventPublisherAware newOrderNotifyEventPublisherAware;

	@Autowired
	private WebSocketService webSocketService;

	@Autowired
	PlatformTransactionManager dataSourceTransactionManager;


	@Async("myAsync")
	public void sseSend(String id, Object payload) {
		MessageBody<Object> messageBody = new MessageBody<>();
		messageBody.setFrom("SSE");
		messageBody.setTo(id);
		messageBody.setPayload(payload);
		messageBody.setTimestamp(System.currentTimeMillis());

		for(int i=0; i<3; i++) {
			if(Globals.sseEmitters.containsKey(id)) {
				Optional.of(Globals.sseEmitters.get(id)).ifPresent(sseEmitter -> {
					NewOrderNotifyEvent newOrderNotifyEvent = new NewOrderNotifyEvent(false, id, sseEmitter, messageBody);
					newOrderNotifyEventPublisherAware.publish(newOrderNotifyEvent);
				});
				break;
			} else {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Async("myAsync")
	public void wbSend(String destination, Object payload) {
		WbOutMessage message = new WbOutMessage();
		message.setFrom("Websocket");
		message.setTo(destination);
		message.setPayload(payload);
		message.setTimestamp(System.currentTimeMillis());

		for(int i=0; i<3; i++) {
			if(Globals.webSockets.contains(destination)) {
				webSocketService.sendMessage(destination, message);
				break;
			} else {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/////////////////////ES////////////////////////
    @Async("myAsync")
    public void syncData(String index, String type, Integer pageSize, JpaRepository repository) {
    		elasticsearchService.syncData(index, type, pageSize, repository);
    }

    @Async("myAsync")
    public void insertEsData(String index, String type, Object data) {
    	elasticsearchService.insertData(index, type, data);
    }
    
    @Async("myAsync")
    public void updateEsData(String index, String type, Object data) {
    	elasticsearchService.updateData(index, type, data);
    }
    
    @Async("myAsync")
    public void deleteEsData(String index, String type, String id) {
    	elasticsearchService.deleteData(index, type, id);
    }


    /////////////////////COS///////////////////////
	@Async("myAsync")
	public void rename(String key, String newFileName, String subAddress) {
		COSClient cosClient = null;
		try {
			cosClient = blogBeanConfig.getCosClient();
			String bucketName = blogProperties.getBucketName();

			//先复制，记录下成功复制的key
			String srcKey = key;
			String temp = key.substring(key.lastIndexOf("/") + 1);
			String id = temp.substring(0, temp.indexOf("-"));
			String destKey = key.substring(0, key.lastIndexOf("/") + 1) + id + "-" + newFileName;
			try {
				CopyObjectResult copyObjectResult = cosClient.copyObject(bucketName, srcKey, bucketName, destKey);
				String eTrag = copyObjectResult.getETag();
				//cos 删除
				cosClient.deleteObject(bucketName, key);
			} catch (Exception e) {
				logger.error("rename->重命名失败，请确认以下COS对象的key（修改成："+destKey+"）是否修改成功：" + key, e);
//				return new BaseResp(Globals.CODE_1, Globals.MSG_1, key);
				this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1, key));
				return;
			}

			CosResource cosResource = cosResourceRepository.findFirstByKey(key);
			if(cosResource != null) {
				//mysql 修改
				//手动开启事务，不要用注解
				TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
				try {
					cosResource.setKey(destKey);
					cosResource.setFileName(newFileName);
					cosResource.setLastModified(new Date());
					cosResourceRepository.save(cosResource);
					dataSourceTransactionManager.commit(transactionStatus);
				} catch (Exception e) {
					logger.error("rename->重命名失败，请确认以下COS对象的key（修改成："+destKey+"）是否修改成功：" + key, e);
//					return new BaseResp(Globals.CODE_2, Globals.MSG_2, key);
					this.wbSend(subAddress, new BaseResp(Globals.CODE_2, Globals.MSG_2, key));
					return;
				}

				//es 修改
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					BeanUtils.bean2Map(cosResource, map);
					boolean flag = elasticsearchHelper.updateData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, cosResource.getId().toString(), map);
					if(!flag) throw new Exception();
				} catch (Exception e) {
					logger.error("rename->重命名失败，请确认以下COS对象的ke（修改成："+destKey+"）是否修改成功：" + key, e);
//					return new BaseResp(Globals.CODE_3, Globals.MSG_3, key);
					this.wbSend(subAddress, new BaseResp(Globals.CODE_3, Globals.MSG_3, key));
					return;
				}
			}

		} catch (Exception e) {
			logger.error("rename->重命名失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1, key));
			return;

		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}
//		return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		this.wbSend(subAddress, new BaseResp(Globals.CODE_0, Globals.MSG_0));
	}

	@Async("myAsync")
	public void batchMove(String keysStr, String destDirPath, String subAddress) {
		COSClient cosClient = null;
		try {
			cosClient = blogBeanConfig.getCosClient();
			String bucketName = blogProperties.getBucketName();

			//先复制，记录下成功复制的key
			List<String> keys = Arrays.asList(keysStr.split(","));
			List<String> copiedKeys = new ArrayList<>();
			for(String key : keys) {
				String srcKey = key;
				String destKey = destDirPath + key.substring(key.lastIndexOf("/") + 1);
				if(!srcKey.equals(destKey)) {
					try {
						CopyObjectResult copyObjectResult = cosClient.copyObject(bucketName, srcKey, bucketName, destKey);
						String eTrag = copyObjectResult.getETag();
						copiedKeys.add(srcKey);
					} catch (CosServiceException e) {
						e.printStackTrace();
					} catch (CosClientException e) {
						e.printStackTrace();
					}
				}
			}

			List<CosResource> cosResources = cosResourceRepository.findAllByKeyIn(copiedKeys);
			if(cosResources != null && cosResources.size() > 0) {
				//cos 删除
				DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
				ArrayList<KeyVersion> keyList = new ArrayList<>();
				cosResources.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
				deleteObjectsRequest.setKeys(keyList);

				List<DeletedObject> deleteObjects = new ArrayList<>();
				List<DeleteError> deleteErrors = new ArrayList<>();
				List<String> deletedErrorKeys = new ArrayList<>();
				try {
					DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
					deleteObjects = deleteObjectsResult.getDeletedObjects();
				} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
					deleteObjects = mde.getDeletedObjects();
					deleteErrors = mde.getErrors();
					deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
					logger.error("batchMove->批量删除失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
				} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
					logger.error("batchMove->批量删除失败，可能是连接问题！", e);
				}

				//mysql 修改
				//手动开启事务，不要用注解
				TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
				try {
					String targetRootPath = destDirPath.substring(0, destDirPath.indexOf("/") + 1);
					for (CosResource cos : cosResources) {
						cos.setLastModified(new Date());
						cos.setKey(destDirPath + cos.getKey().substring(cos.getKey().lastIndexOf("/") + 1));
						if(!targetRootPath.equals(cos.getRootPath())) {
							cos.setRootPath(targetRootPath);
						}
					}
					cosResourceRepository.save(cosResources);
					dataSourceTransactionManager.commit(transactionStatus);
				} catch (Exception e) {
					logger.error("batchMove->批量修改mysql中数据失败，请确认以下COS对象的key前缀（修改成："+destDirPath+"）是否修改成功：" + copiedKeys, e);
//					return new BaseResp(Globals.CODE_2, Globals.MSG_2, copiedKeys);
					this.wbSend(subAddress, new BaseResp(Globals.CODE_2, Globals.MSG_2, copiedKeys));
					return;
				}

				//es 修改
				try {
					for(CosResource cos : cosResources) {
						Map<String, Object> map = new HashMap<String, Object>();
						BeanUtils.bean2Map(cos, map);
						boolean flag = elasticsearchHelper.updateData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, cos.getId().toString(), map);
						if(!flag) throw new Exception();
					}
				} catch (Exception e) {
					logger.error("batchMove->批量修改es中数据失败，请确认以下COS对象的key前缀（修改成："+destDirPath+"）是否修改成功：" + copiedKeys, e);
//					return new BaseResp(Globals.CODE_3, Globals.MSG_3, copiedKeys);
					this.wbSend(subAddress, new BaseResp(Globals.CODE_3, Globals.MSG_3, copiedKeys));
					return;
				}
			}

		} catch (Exception e) {
			logger.error("batchMove->批量移动对象失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
			return;

		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}
//		return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		this.wbSend(subAddress, new BaseResp(Globals.CODE_0, Globals.MSG_0));
	}

	@Async("myAsync")
	public void batchSetObjectAcl(String keysStr, Integer aclFlag, String subAddress) {
		COSClient cosClient = null;
		String bucketName = null;
		boolean flag = false;
		try {
			List<String> keys = Arrays.asList(keysStr.split(","));
			List<String> setKeys = new ArrayList<>();

			//cos
			try {
				for (String key : keys) {
					cosClient = blogBeanConfig.getCosClient();
					bucketName = blogProperties.getBucketName();
					cosClient.setObjectAcl(bucketName, key, Globals.ACL_MAP.get(aclFlag));
					setKeys.add(key);
				}
			} catch (Exception e) {
				logger.error("batchSetObjectAcl->修改对象权限第1步就失败了：修改COS对象权限失败！", e);
//				return new BaseResp(Globals.CODE_1, Globals.MSG_1);
				this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
				return;
			}

			//mysql
			//手动开启事务，不要用注解
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
			List<CosResource> cosResources = cosResourceRepository.findAllByKeyIn(keys);
			try {
				cosResources.forEach(cosResource -> {
					cosResource.setAclFlag(aclFlag);
					cosResource.setLastModified(new Date());
				});
				cosResourceRepository.save(cosResources);
				dataSourceTransactionManager.commit(transactionStatus); //立马提交，同步到数据库中
			} catch (Exception e) {
				logger.error("batchSetObjectAcl->修改对象权限第2步就失败了：同步信息到mysql失败！", e);
//				return new BaseResp(Globals.CODE_2, Globals.MSG_2);
				this.wbSend(subAddress, new BaseResp(Globals.CODE_2, Globals.MSG_2));
				return;
			}

			//es
			try {
				List<Map<String, Object>> maps = new ArrayList<>();
				cosResources.forEach(cosResource -> {
					Map<String, Object> map = new HashMap<String, Object>();
					BeanUtils.bean2Map(cosResource, map);
					maps.add(map);
				});
				flag = elasticsearchHelper.batchUpdate(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, maps);
				if(!flag) throw new Exception();
			} catch (Exception e) {
				logger.error("batchSetObjectAcl->修改对象权限第3步就失败了：同步信息到es失败！", e);
//				return new BaseResp(Globals.CODE_3, Globals.MSG_3);
				this.wbSend(subAddress, new BaseResp(Globals.CODE_3, Globals.MSG_3));
				return;
			}

		} catch (Exception e) {
			this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
			return;

		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}
		this.wbSend(subAddress, new BaseResp(Globals.CODE_0, Globals.MSG_0));
	}

	@Async("myAsync")
	public void batchDelete(String keysStr, String subAddress) {
		COSClient cosClient = null;
		try {
			cosClient = blogBeanConfig.getCosClient();
			String bucketName = blogProperties.getBucketName();

			List<String> keys = Arrays.asList(keysStr.split(","));
			List<CosResource> cosResources = cosResourceRepository.findAllByKeyIn(keys);

			if(cosResources != null && cosResources.size() > 0) {
				//cos
				//设置要删除的key列表, 最多一次删除1000个
				DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
				ArrayList<KeyVersion> keyList = new ArrayList<>();
				cosResources.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
				deleteObjectsRequest.setKeys(keyList);

				List<DeletedObject> deleteObjects = new ArrayList<>();
				List<DeleteError> deleteErrors = new ArrayList<>();
				List<String> deletedErrorKeys = new ArrayList<>();
				try {
					DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
					deleteObjects = deleteObjectsResult.getDeletedObjects();
				} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
					deleteObjects = mde.getDeletedObjects();
					deleteErrors = mde.getErrors();
					deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
					logger.error("deleteCosObjects->批量删除对象第1步就失败了，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
				} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
					logger.error("deleteCosObjects->批量删除对象第1步就失败了，可能是连接问题！", e);
				}

				//mysql
				List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
				List<Long> ids1 = cosResources.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
				//手动开启事务，不要用注解
				TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
				try {
					if(ids1 != null && ids1.size() > 0) cosResourceRepository.deleteCosResourcesByIdIn(ids1);
					dataSourceTransactionManager.commit(transactionStatus);
				} catch (Exception e) {
					logger.error("deleteCosObjects->批量删除对象第2步就失败了，删除mysql中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
//					return new BaseResp(Globals.CODE_2, Globals.MSG_2, deletedKeys);
					this.wbSend(subAddress, new BaseResp(Globals.CODE_2, Globals.MSG_2, deletedKeys));
					return;
				}

				//es
				List<String> ids2 = ids1.stream().map(id -> id.toString()).collect(Collectors.toList());
				try {
					if(ids2 != null && ids2.size() > 0) elasticsearchHelper.batchDelete(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, ids2);
				} catch (Exception e) {
					logger.error("deleteCosObjects->批量删除对象第3步就失败了，删除ES中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
//					return new BaseResp(Globals.CODE_3, Globals.MSG_3, deletedKeys);
					this.wbSend(subAddress, new BaseResp(Globals.CODE_3, Globals.MSG_3, deletedKeys));
					return;
				}
			}

		} catch (Exception e) {
			logger.error("deleteCosObjects->批量删除对象失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
			this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
			return;
		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}

//		return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		this.wbSend(subAddress, new BaseResp(Globals.CODE_0, Globals.MSG_0));
	}

	@Async("myAsync")
	public void refreshTagsWordFrequencyCount() {
		List<WordFrequency> list = new ArrayList<>();
		try {
			redisTemplate.delete("tag-cloud");
			List<String> allTags = cosResourceRepository.findAllTags();
			Map<String, Integer> map = new HashMap<>();
			for (String everyTags : allTags) {
				String[] tags = everyTags.split(",");
				for (String tag : tags) {
					if(map.containsKey(tag)) {
						map.put(tag, map.get(tag) + 1);
					} else {
						map.put(tag, 1);
					}
				}
			}

			for(Map.Entry<String, Integer> en : map.entrySet()) {
				list.add(new WordFrequency(en.getKey(), en.getValue()));
			}
			Collections.sort(list, new Comparator<WordFrequency>() {
				@Override
				public int compare(WordFrequency o1, WordFrequency o2) {
					return o2.getWeight() - o1.getWeight();
				}
			});

			int end =  list.size() >= 30 ? 30 : list.size();
			list = list.subList(0, end);
			redisTemplate.opsForValue().set("tags-cloud", list);
		} catch (Exception e) {
			logger.error("refreshTagsWordFrequencyCount->刷新标签的词频统计结果失败：", e);
		}
	}

	@Async("myAsync")
	public void deleteCosObject(String bucketName, String key) {
		COSClient client = null;
		try {
			client = blogBeanConfig.getCosClient();
			client.deleteObject(bucketName, key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(client != null) {
				client.shutdown();
			}
		}
	}

	@Async("myAsync")
	public void deleteCosDirectory(String targetDirPath, String subAddress) {
		COSClient cosClient = null;
		try {
			cosClient = blogBeanConfig.getCosClient();
			String bucketName = blogProperties.getBucketName();

			//1、先删除文件对象
			int pageIndex = 0;
			Page<CosResource> page = null;
			do {
				PageRequest pageRequest  = new PageRequest(pageIndex++, 500);
				page = cosResourceRepository.findByKeyStartsWithAndCosType(targetDirPath, 0, pageRequest);

				if(page != null && page.hasContent()) {
					//cos
					//设置要删除的key列表, 最多一次删除1000个
					DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
					ArrayList<KeyVersion> keyList = new ArrayList<>();
					List<CosResource> contents = page.getContent();
					contents.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
					deleteObjectsRequest.setKeys(keyList);

					List<DeletedObject> deleteObjects = new ArrayList<>();
					List<DeleteError> deleteErrors = new ArrayList<>();
					List<String> deletedErrorKeys = new ArrayList<>();
					try {
						DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
						deleteObjects = deleteObjectsResult.getDeletedObjects();
					} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
						deleteObjects = mde.getDeletedObjects();
						deleteErrors = mde.getErrors();
						deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
						logger.error("deleteDirectory->删除目录第1步就失败了，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
					} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
						logger.error("deleteDirectory->删除目录第1步就失败了，删除COS目录失败，可能是连接问题！", e);
					}

					//mysql
					List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
					List<Long> ids1 = contents.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
					//手动开启事务，不要用注解
					TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
					try {
						if(ids1 != null && ids1.size() > 0) cosResourceRepository.deleteCosResourcesByIdIn(ids1);
						dataSourceTransactionManager.commit(transactionStatus);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第2步就失败了，删除mysql中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}

					//es
					List<String> ids2 = ids1.stream().map(id -> id.toString()).collect(Collectors.toList());
					try {
						if(ids2 != null && ids2.size() > 0) elasticsearchHelper.batchDelete(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, ids2);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第3步就失败了，删除ES中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}
				}
			} while (page != null && page.hasNext());


			//2、再删除虚拟目录
			pageIndex = 0;
			page = null;
			do {
				PageRequest pageRequest  = new PageRequest(pageIndex++, 500);
				page = cosResourceRepository.findByKeyStartsWithAndCosType(targetDirPath, 1, pageRequest);

				if(page != null && page.hasContent()) {
					//用于装载目录路径，且排序（子目录在前，父目录在后）
					Set<CosResource> pathSet = new TreeSet<CosResource>(new Comparator<CosResource>() {
						@Override
						public int compare(CosResource o1, CosResource o2) {
							int diff = o2.getKey().split("/").length - o1.getKey().split("/").length;
							return diff == 0 ? o2.getKey().compareTo(o1.getKey()) : diff;
						}
					});

					//cos
					//设置要删除的key列表, 最多一次删除1000个
					DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
					ArrayList<KeyVersion> keyList = new ArrayList<>();
					pathSet.addAll(page.getContent());
					pathSet.forEach(cosResource -> keyList.add(new KeyVersion(cosResource.getKey())));
					deleteObjectsRequest.setKeys(keyList);

					List<DeletedObject> deleteObjects = new ArrayList<>();
					List<DeleteError> deleteErrors = new ArrayList<>();
					List<String> deletedErrorKeys = new ArrayList<>();
					try {
						DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
						deleteObjects = deleteObjectsResult.getDeletedObjects();
					} catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
						deleteObjects = mde.getDeletedObjects();
						deleteErrors = mde.getErrors();
						deletedErrorKeys = deleteErrors.stream().map(DeleteError::getKey).collect(Collectors.toList());
						logger.error("deleteDirectory->删除目录第4步就失败了，请确认以下COS对象是否删除成功：" + deletedErrorKeys, mde);
					} catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
						logger.error("deleteDirectory->删除目录第4步就失败了，删除COS目录失败，可能是连接问题！", e);
					}

					//mysql
					List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
					List<Long> ids1 = pathSet.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
					//手动开启事务，不要用注解
					TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(new DefaultTransactionDefinition());
					try {
						if(ids1 != null && ids1.size() > 0) cosResourceRepository.deleteCosResourcesByIdIn(ids1);
						dataSourceTransactionManager.commit(transactionStatus);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第5步就失败了，删除mysql中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}

					//es
					List<String> ids2 = ids1.stream().map(id -> id.toString()).collect(Collectors.toList());
					try {
						if(ids2 != null && ids2.size() > 0) elasticsearchHelper.batchDelete(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, ids2);
					} catch (Exception e) {
						logger.error("deleteDirectory->删除目录第6步就失败了，删除ES中数据失败，请确认以下COS对象是否删除成功：" + deletedErrorKeys.addAll(deletedKeys), e);
					}
				}
			} while (page != null && page.hasNext());

			try {
				boolean flag1 = cosClient.doesObjectExist(bucketName, targetDirPath);
				boolean flag2 = cosResourceRepository.findFirstByKeyStartsWith(targetDirPath) != null;
				boolean flag3 = elasticsearchHelper.searchListData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, "key", targetDirPath) != null;
				if(!flag1 && !flag2 && !flag3) {
					logger.info("deleteDirectory->删除成功：" + targetDirPath);
//					this.sseSend(eventId, new BaseResp(Globals.CODE_0, Globals.MSG_0));
					this.wbSend(subAddress, new BaseResp(Globals.CODE_0, Globals.MSG_0));
				} else {
					logger.info("deleteDirectory->删除失败："+ targetDirPath);
//					this.sseSend(eventId, new BaseResp(Globals.CODE_1, Globals.MSG_1));
					this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
				}
			} catch (Exception e) {
				this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
				throw new Exception();
			}

		} catch (Exception e) {
			logger.error("deleteDirectory->删除目录失败，原因可能是COS或者MYSQL或者ES链接出了问题！", e);
//			this.sseSend(eventId, new BaseResp(Globals.CODE_1, Globals.MSG_1));
			this.wbSend(subAddress, new BaseResp(Globals.CODE_1, Globals.MSG_1));
		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}
	}

}