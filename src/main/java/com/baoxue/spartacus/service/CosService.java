package com.baoxue.spartacus.service;

import com.alibaba.fastjson.JSON;
import com.baoxue.spartacus.config.BlogBeanConfig;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;
import com.baoxue.spartacus.pojo.CosResource;
import com.baoxue.spartacus.pojo.PageEntity;
import com.baoxue.spartacus.pojo.WordFrequency;
import com.baoxue.spartacus.repository.CosResourceRepository;
import com.baoxue.spartacus.repository.ElasticsearchHelper;
import com.baoxue.spartacus.task.AsyncTask;
import com.baoxue.spartacus.utils.BeanUtils;
import com.baoxue.spartacus.utils.CommonUtils;
import com.baoxue.spartacus.utils.Snowflake;
import com.baoxue.spartacus.utils.ZipUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 腾讯云对象存储
 * 
 * 注意，上传对象的命名方式用这种，如：2019-01-30-id-xxx.png
 *
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年2月11日 上午11:08:48
 */
@Component
public class CosService {
	private static Logger logger = Logger.getLogger(CosService.class);

	@Lazy
	@Autowired
    AsyncTask task;
	
	@Autowired
    private BlogProperties blogProperties;
	
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	
	@Autowired
	private CosResourceRepository cosResourceRepository;
	
	@Autowired
	private ElasticsearchHelper elasticsearchHelper;

	@Autowired
	BlogBeanConfig blogBeanConfig;

	@Autowired
	ElasticsearchService elasticsearchService;


	/**
	 * 高级搜索
	 * @param searchContent
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws BlogException
	 */
	public BaseResp search(String searchContent, Integer cosType, String rootPath, int currentPage, int pageSize) throws BlogException {
		String highlightFields = "fileName";
		String matchFields = "fileName,tags";
		try {
			Map<String, Object> mustMatchs = new HashMap<>();
			mustMatchs.put("cosType", cosType);
			mustMatchs.put("rootPath", rootPath);
			PageEntity pageEntity = elasticsearchHelper.searchPageData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME,
					currentPage -1, pageSize, 0, 0, null, "lastModified", highlightFields, mustMatchs, searchContent, matchFields);
			List<Map<String, Object>> records = (List<Map<String, Object>>)pageEntity.getRecords();
			records.forEach(r -> { r.put("url", blogProperties.getBaseUrl() + "/" + r.get("key")); });
			pageEntity.setRecords(records);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageEntity);
		} catch (Exception e) {
			logger.info("搜索失败！", e);
		}

		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 数据同步到ES
	 * @return
	 */
	public BaseResp syncData() {
		try {
			elasticsearchService.syncData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, 500, cosResourceRepository);
		} catch (Exception e) {
			logger.error("syncData->数据同步异常！", e);
			return new BaseResp(Globals.CODE_1, Globals.MSG_1);
		}

		return new BaseResp(Globals.CODE_0, Globals.MSG_0);
	}

	/**
	 * 重命名
	 *
	 * @param key
	 * @param newFileName
	 * @return
	 */
//	@Transactional
	public BaseResp rename(String key, String newFileName, String subAddress) {
		try {
			task.rename(key, newFileName, subAddress);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("rename->重命名命令执行失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}


	/**
	 * 批量移动cos对象
	 *
	 * @param keysStr 多个key使用英文逗号分隔
	 * @param destDirPath 目的目录的路径，比如 'image/111/222/'
	 * @return
	 */
//	@Transactional
	public BaseResp batchMove(String keysStr, String destDirPath, String subAddress) {
		try {
			task.batchMove(keysStr, destDirPath, subAddress);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchMove->批量移动命令执行失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 批量下载
	 *
	 * @param keysStr
	 * @return
	 */
	public BaseResp batchDownload(String keysStr) {
		COSClient cosClient = null;
		String bucketName = null;
		try {
			String[] keys = keysStr.split(",");
			List<Map<String, byte[]>> bytesList = new ArrayList<>();
			for(String key : keys) {
				try {
					//获取图片流
					cosClient = blogBeanConfig.getCosClient();
					bucketName = blogProperties.getBucketName();
					GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
					COSObject cosObject = cosClient.getObject(getObjectRequest);
					ObjectMetadata objectMetadata = cosObject.getObjectMetadata();
					COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

					//转换成base64字符串
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buffer = new byte[4096];
					int n = 0;
					while (-1 != (n = cosObjectInput.read(buffer))) {
						output.write(buffer, 0, n);
					}

					String fileName = key.substring(key.lastIndexOf("/") + 1);
					Map<String, byte[]> map = new HashMap<>();
					map.put(fileName, output.toByteArray());
					bytesList.add(map);

				} catch (Exception e) {
					logger.error("batchDownload->下载文件失败：" + key, e);
					throw  new Exception();
				}
			}

			//发给前端
			String fileName = Snowflake.generateId() +".zip";
			Map<String, Object> map = new HashMap<>();
			map.put("base64", ZipUtils.batchZipByteArrayOutputStream(bytesList));
			map.put("contentType", "application/x-zip-compressed");
			map.put("fileName", fileName);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, map);

		} catch (Exception e) {
			logger.error("batchDownload->批量下载文件后，压缩失败：" + keysStr, e);
		}
		finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}


	/**
	 * 下载文件，返回base64编码数据
	 *
	 * @param key
	 * @return
	 */
	public BaseResp download(String key) {
		COSClient cosClient = null;
		String bucketName = null;
		try {
			//获取图片流
			cosClient = blogBeanConfig.getCosClient();
			bucketName = blogProperties.getBucketName();
			GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
			COSObject cosObject = cosClient.getObject(getObjectRequest);
			ObjectMetadata objectMetadata = cosObject.getObjectMetadata();
			COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

			//转换成base64字符串
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = cosObjectInput.read(buffer))) {
				output.write(buffer, 0, n);
			}
			BASE64Encoder encoder = new BASE64Encoder();
			String base64 = encoder.encode(output.toByteArray());

			//发给前端
			String fileName = key.substring(key.lastIndexOf("/") + 1);
			Map<String, Object> map = new HashMap<>();
			map.put("base64", base64);
			map.put("contentType", objectMetadata.getContentType());
			map.put("fileName", fileName);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, map);

		} catch (Exception e) {
			logger.error("download->下载文件失败：" + key, e);
		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 获取标签的 词频 统计列表
	 *
	 * @return
	 */
	public BaseResp listTags() {
		List<WordFrequency> list = new ArrayList<>();
		try {
			if(redisTemplate.hasKey("tags-cloud")) {
				list = JSON.parseArray(redisTemplate.opsForValue().get("tags-cloud").toString(), WordFrequency.class);
			} else {
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
			}
			redisTemplate.opsForValue().set("tags-cloud", list);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, list);
		} catch (Exception e) {
			logger.error("listTags->获取标签的词频统计结果失败：", e);
		}

		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 先上传到COS，成功后再写到MYSQL，成功后再写到ES
	 *  
	 * @author lvchao 2019年5月23日
	 * @param parentDirPath
	 * @param fileName
	 * @param tags
	 * @param fileBytes
	 * @return
	 * @throws BlogException 
	 */
	@Transactional
	public BaseResp webUploader(String parentDirPath, Long parentId, String fileName, String tags, byte[] fileBytes) {
		COSClient cosClient = null;
		String bucketName = null;
		String key = null;
		String region = blogProperties.getBucketRegion();
		CosResource cosResource = new CosResource();
		Long id = Snowflake.generateId();
		int aclFlag = 2;
		boolean flag = false;
		String contentType = null;
        try {
        	//COS
        	try {
				cosClient = blogBeanConfig.getCosClient();
	            // bucket名需包含appid
	            bucketName = blogProperties.getBucketName();
	            // 上传到COS后相对bucket的路径，对象key包含多级目录也没事，如果包含的目录不存在，会自动创建
		        key = parentDirPath + id + "-" + fileName;
	            // 获取文件流
	            InputStream inputStream = new ByteArrayInputStream(fileBytes);
	            // 生成元数据
	            ObjectMetadata objectMetadata = new ObjectMetadata();
	            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
	            objectMetadata.setContentLength(fileBytes.length);
	            // 生成存储对象
	            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
	            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia), 近线(nearline) 
	            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
	            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
				contentType = putObjectResult.getMetadata().getContentType();
	            //设置访问权限
	            aclFlag = cosClient.getObjectAcl(bucketName, parentDirPath).getGrantsAsList().size();
				cosClient.setObjectAcl(bucketName, key, Globals.ACL_MAP.get(aclFlag));
				flag = true; //COS不报错即是成功
			} catch (Exception e) {
        		logger.error("webUploader->上传文件第1步就失败了：上传文件至COS失败！", e);
				task.deleteCosObject(bucketName, key);
				return new BaseResp(Globals.CODE_1, "上传文件第1步就失败了：上传文件至COS失败！");
			}
            
            //MYSQL
        	try {
	            cosResource.setId(id);
	            cosResource.setParentId(parentId);
	            cosResource.setRegion(region);
	            cosResource.setBucketName(bucketName);
	            cosResource.setFileName(fileName);
	            cosResource.setKey(key);
	            cosResource.setStatus(0);
	            cosResource.setContentType(contentType);
				cosResource.setCosType(0);
				cosResource.setRootPath(parentDirPath.substring(0, parentDirPath.indexOf("/")+1));
	            cosResource.setTags(tags.replace("，",","));
	            cosResource.setAclFlag(aclFlag);
	            cosResource.setLastModified(new Date());
	            flag = CommonUtils.isNull(cosResourceRepository.saveAndFlush(cosResource));
	            if(flag) throw new Exception();
        	} catch (Exception e) {
				logger.error("webUploader->上传文件第2步就失败了：同步文件信息Mysql失败！", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
				task.deleteCosObject(bucketName, key);
				return new BaseResp(Globals.CODE_1, "上传文件第2步就失败了：同步文件信息Mysql失败！");
			}
            
            //ES
        	try {
        		Map<String, Object> map = new HashMap<String, Object>();
				BeanUtils.bean2Map(cosResource, map);
				flag = elasticsearchHelper.insertData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME,  map.get("id").toString(), map);
				if(!flag)
					throw new Exception();
				else
					task.refreshTagsWordFrequencyCount();
        	} catch (Exception e) {
				logger.error("webUploader->上传文件第3步就失败了：同步文件信息ES失败！", e);
				task.deleteEsData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, id.toString());
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
				task.deleteCosObject(bucketName, key);
				return new BaseResp(Globals.CODE_1, "上传文件第3步就失败了：同步文件信息ES失败！");
			}

        } finally {
        	if(cosClient != null) {
        		cosClient.shutdown();
        	}
		}
        
        return new BaseResp(Globals.CODE_0, Globals.MSG_0);
    }

	/**
	 * 分页获取 cos 对象数据（注意，不包含虚拟目录对象）
	 *
	 * @param isRecursive
	 * @param dirPath 例如 'image/工作/public/'
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public BaseResp listObjects(boolean isRecursive, String dirPath, Integer currentPage, Integer pageSize, String tag) {
		PageEntity pageEntity = new PageEntity();
		pageEntity.setCurrentPage(currentPage);
		pageEntity.setPageSize(pageSize);
		List<CosResource> records = null;
		Integer total = 0;
		try {
			if(isRecursive) {
				if(tag != null && !"".equals(tag)) {
					records = cosResourceRepository.getRecursiveCosResourcesByTag(dirPath, (currentPage - 1) * pageSize, pageSize, tag);
					total = cosResourceRepository.getRecursiveCosResourcesCountByTag(dirPath, tag);
				} else {
					records = cosResourceRepository.getRecursiveCosResources(dirPath, (currentPage - 1) * pageSize, pageSize);
					total = cosResourceRepository.getRecursiveCosResourcesCount(dirPath);
				}
			} else {
				if(tag != null && !"".equals(tag)) {
					records = cosResourceRepository.getDirectCosResourcesByTag(dirPath, (currentPage - 1) * pageSize, pageSize, tag);
					total = cosResourceRepository.getDirectCosResourcesCountByTag(dirPath, tag);
				} else {
					records = cosResourceRepository.getDirectCosResources(dirPath, (currentPage - 1) * pageSize, pageSize);
					total = cosResourceRepository.getDirectCosResourcesCount(dirPath);
				}
			}
			records.forEach(r -> r.setUrl(blogProperties.getBaseUrl() + "/" + r.getKey()));
			pageEntity.setTotal(total);
			pageEntity.setTotalPages((total + pageSize - 1) / pageSize);
			pageEntity.setRecords(records);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageEntity);
		} catch (Exception e) {
			logger.error("listObjects->获取分页数据失败：", e);
		}

		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
	 * 批量设置COS对象的ACL权限
	 *  
	 * @author lvchao 2019年1月22日
	 * @param keysStr
	 * @param aclFlag
	 * @return
	 */
//	@Transactional
	public BaseResp batchSetObjectAcl(String keysStr, Integer aclFlag, String subAddress) {
		try {
			task.batchSetObjectAcl(keysStr, aclFlag, subAddress);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchSetObjectAcl->批量设置权限命令执行失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 新建COS虚拟目录，默认设置权限为 公有读私有写
	 *
	 * @param parentDirPath
	 * @param parentId
	 * @param newDirName
	 * @return
	 */
	@Transactional
	public BaseResp createDirectory(String parentDirPath, Long parentId, String newDirName) {
		COSClient cosClient = null;
		CosResource cosResource = new CosResource();
		String key = parentDirPath + newDirName + "/";
		String bucketName = null;
		Long id = Snowflake.generateId();
		boolean flag = false;
		try {
			//COS
			try {
				cosClient = blogBeanConfig.getCosClient();
				// bucket名需包含appid
				bucketName = blogProperties.getBucketName();
				// 目录对象即是一个/结尾的空文件，上传一个长度为 0 的 byte 流
				InputStream input = new ByteArrayInputStream(new byte[0]);
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentLength(0);
				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, input, objectMetadata);
				PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
				String etag = putObjectResult.getETag();
				// 设置默认权限为公有读私有写（不设置的话Object的权限默认继承父节点的权限）
				cosClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
			} catch (Exception e) {
				logger.error("createDirectory->新建目录第1步就失败了：原因是在COS端远程创建新目录失败！", e);
				return new BaseResp(Globals.CODE_1, "新建目录第1步就失败了：原因是在COS端远程创建新目录失败！");
			}

			//MYSQL
			try {
				cosResource.setId(id);
				cosResource.setParentId(parentId);
				cosResource.setKey(key);
				cosResource.setFileName(newDirName);
				cosResource.setRegion(blogProperties.getBucketRegion());
				cosResource.setBucketName(blogProperties.getBucketName());
				cosResource.setStatus(0);
				cosResource.setCosType(1); //目录
				cosResource.setAclFlag(2);
				cosResource.setLastModified(new Date());
				flag = CommonUtils.isNull(cosResourceRepository.saveAndFlush(cosResource));
				if(flag) throw new Exception();
			} catch (Exception e) {
				logger.error("createDirectory->新建目录第2步就失败了：原因是向MYSQL中写入数据失败！", e);
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
				task.deleteCosObject(bucketName, key);
				return new BaseResp(Globals.CODE_1, "新建目录第2步就失败了：原因是向MYSQL中写入数据失败！");
			}

			//ES
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				BeanUtils.bean2Map(cosResource, map);
				flag = elasticsearchHelper.insertData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME,  map.get("id").toString(), map);
				if(!flag) throw new Exception();
			} catch (Exception e) {
				logger.error("createDirectory->新建目录第3步就失败了：原因是向ES中写入数据失败！", e);
				task.deleteEsData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, id.toString());
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
				task.deleteCosObject(bucketName, key);
				return new BaseResp(Globals.CODE_1, "新建目录第3步就失败了：原因是向ES中写入数据失败！");
			}

		} catch (Exception e) {
			logger.error("新建COS目录失败！", e);

		} finally {
			if(cosClient != null) {
				cosClient.shutdown();
			}
		}

		return new BaseResp(Globals.CODE_0, Globals.MSG_0);
	}

	/**
	 * 异步删除COS目录对象
	 *  
	 * @author lvchao 2019年1月17日
	 * @return
	 */
	// @Transactional
	public BaseResp deleteDirectory(String targetDirPath, String subAddress) {
		try {
			task.deleteCosDirectory(targetDirPath, subAddress);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("deleteDirectory->删除命令执行失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 批量删除 cos 对象
	 * @param keysStr 多个key，用英文逗号分隔
	 * @return
	 */
//	@Transactional
	public BaseResp batchDelete(String keysStr, String subAddress) {
		try {
			task.batchDelete(keysStr, subAddress);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchDelete->批量删除命令执行失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 根据COS中某个bucket下的某个根目录，生成该根目录的目录树
	 * 
	 * COS Java SDK v5没有目录操作的方法，这里自定义递归遍历某个根目录获取所有objects耗时耗力，待优化
	 *  
	 * @author lvchao 2019年1月11日
	 * @return
	 */
	public BaseResp getDirectoryTree(String rootDirPath) {
 		try {
			List<CosResource> nodeList = null;
			if(CommonUtils.isNull(rootDirPath)) {
				nodeList = cosResourceRepository.findAllByCosType(1);
			} else {
				nodeList = cosResourceRepository.findAllByKeyStartsWithAndCosType(rootDirPath, 1);
			}
 			return new BaseResp(Globals.CODE_0, Globals.MSG_0, nodeList);

        } catch (Exception e) {
            logger.error("生成COS目录树失败！", e);
        }
 		return new BaseResp(Globals.CODE_1, Globals.MSG_1, null);
    }


	/**
	 * 以文件流方式上传
	 *  
	 * @author lvchao 2018年2月11日 上午11:12:10
	 * @param fileBytes
	 * @return 返回文件传到COS后的访问路径
	 */
	public BaseResp fileUpload(String fileName, byte[] fileBytes) {
        
		COSClient cosClient = null;
        try {
            cosClient = blogBeanConfig.getCosClient();
            String bucketName = blogProperties.getBucketName();
            
            // 上传到COS后相对bucket的路径，对象key包含多级目录也没事，如果包含的目录不存在，会自动创建
	        String dirKey = blogProperties.getBasePath() + CommonUtils.getDateString("yyyy-MM-dd") + "/";
	        String key = dirKey + Snowflake.generateId() + "-" + fileName;
            
            // 获取文件流
            InputStream inputStream = new ByteArrayInputStream(fileBytes);
            // 生成元数据
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(fileBytes.length);
            // 生成存储对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia), 近线(nearline) 
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        	String imgUrl = blogProperties.getBaseUrl() + "/" + key;
        	return new BaseResp(Globals.CODE_0, Globals.MSG_0, imgUrl);
        } catch (Exception e) {
            logger.error("上传失败！", e);
        } finally {
        	if(cosClient != null) {
        		cosClient.shutdown();
        	}
		}
        return new BaseResp(Globals.CODE_1, Globals.MSG_1, blogProperties.getDefaultUrl());
    }
	

    /**
	 * 判断COS对象是否存在
	 *  
	 * @author lvchao 2019年5月22日
	 * @param key
	 * @return
	 * @throws CosClientException
	 * @throws CosServiceException
	 */
	public boolean isExist(String key) {
		COSClient cosClient = null;
        try {
			cosClient = blogBeanConfig.getCosClient();
            String bucketName = blogProperties.getBucketName();
            // 获取 COS 文件属性
    		ObjectMetadata objectMetadata = cosClient.getObjectMetadata(bucketName, key);
    		return true;
        } catch (CosServiceException e) {
        	return false;
        } finally {
        	if(cosClient != null) {
        		cosClient.shutdown();
        	}
		}
	}

}
