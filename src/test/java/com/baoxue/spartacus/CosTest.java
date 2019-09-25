package com.baoxue.spartacus;

import com.baoxue.spartacus.config.BlogBeanConfig;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.globals.Globals;
import com.baoxue.spartacus.pojo.CosResource;
import com.baoxue.spartacus.repository.CosResourceRepository;
import com.baoxue.spartacus.repository.ElasticsearchHelper;
import com.baoxue.spartacus.task.AsyncTask;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.MultiObjectDeleteException;
import com.qcloud.cos.exception.MultiObjectDeleteException.DeleteError;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.DeleteObjectsRequest.KeyVersion;
import com.qcloud.cos.model.DeleteObjectsResult.DeletedObject;
import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author C
 * @Date 2019/8/15 0:16
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class CosTest {

    private static Logger logger = Logger.getLogger(CosTest.class);

    @Autowired
    private AsyncTask task;

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


//    @Test
//    @Transactional
    public void deleteDirectory(/*String targetDirPath*/) {
        String targetDirPath = "image/1111/";

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
                    try {
                        DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
                        deleteObjects = deleteObjectsResult.getDeletedObjects();
                    } catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
                        deleteObjects = mde.getDeletedObjects();
                        deleteErrors = mde.getErrors();
                        logger.error("deleteDirectory->删除目录第1步就失败了：删除COS目录时，以下COS对象删除失败：", mde);
                    } catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
                        logger.error("deleteDirectory->删除目录第1步就失败了：删除COS目录失败，可能是连接问题！", e);
                    }

                    //mysql
                    List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
                    List<Long> ids1 = contents.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
                    try {
                        if(ids1 != null && ids1.size() > 0) cosResourceRepository.deleteCosResourcesByIdIn(ids1);
                    } catch (Exception e) {
                        logger.error("deleteDirectory->删除目录第2步就失败了：删除mysql中数据失败，请手动删除mysql中_key为以下值的数据：", e);
                    }

                    //es
                    List<String> ids2 = ids1.stream().map(id -> id.toString()).collect(Collectors.toList());
                    try {
                        if(ids2 != null && ids2.size() > 0) elasticsearchHelper.batchDelete(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, ids2);
                    } catch (Exception e) {
                        logger.error("deleteDirectory->删除目录第3步就失败了：删除ES中数据失败，请手动删除ES中_key为以下值的数据：", e);
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
                    try {
                        DeleteObjectsResult deleteObjectsResult = cosClient.deleteObjects(deleteObjectsRequest);
                        deleteObjects = deleteObjectsResult.getDeletedObjects();
                    } catch (MultiObjectDeleteException mde) { // 如果部分删除成功部分失败, 返回MultiObjectDeleteException
                        deleteObjects = mde.getDeletedObjects();
                        deleteErrors = mde.getErrors();
                        logger.error("deleteDirectory->删除目录第4步就失败了：删除COS以下虚拟目录失败：", mde);
                    } catch (Exception e) { // 如果是其他错误，例如参数错误， 身份验证不过等会抛出 CosServiceException
                        logger.error("deleteDirectory->删除目录第4步就失败了：删除COS目录失败，可能是连接问题！", e);
                    }

                    //mysql
                    List<String> deletedKeys = deleteObjects.stream().map(DeletedObject::getKey).collect(Collectors.toList());
                    List<Long> ids1 = pathSet.stream().filter(c -> deletedKeys.contains(c.getKey())).map(CosResource::getId).collect(Collectors.toList());
                    try {
                        if(ids1 != null && ids1.size() > 0) cosResourceRepository.deleteCosResourcesByIdIn(ids1);
                    } catch (Exception e) {
                        logger.error("deleteDirectory->删除目录第5步就失败了：删除mysql中数据失败，请手动删除mysql中_key为以下值的数据：", e);
                    }

                    //es
                    List<String> ids2 = ids1.stream().map(id -> id.toString()).collect(Collectors.toList());
                    try {
                        if(ids2 != null && ids2.size() > 0) elasticsearchHelper.batchDelete(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, ids2);
                    } catch (Exception e) {
                        logger.error("deleteDirectory->删除目录第5步就失败了：删除ES中数据失败，请手动删除ES中_key为以下值的数据：", e);
                    }
                }
            } while (page != null && page.hasNext());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

}
