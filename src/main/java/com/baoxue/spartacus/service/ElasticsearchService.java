package com.baoxue.spartacus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baoxue.spartacus.repository.ElasticsearchHelper;
import com.baoxue.spartacus.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2019年4月3日 下午6:28:25
 */
@Service
public class ElasticsearchService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    ElasticsearchHelper elasticsearchHelper;
	
	
	
	public boolean createIndex(String index, String type) {
		boolean flag = false;
	    try {
	    	logger.info("createIndex->Create index '"+index+"' started...");
	    	
	    	if(!elasticsearchHelper.isIndexExist(index)) {
	    		flag = elasticsearchHelper.createIndex(index, type);
				if(flag) {
					logger.info("createIndex->Create succeeded!");
				} else {
					logger.info("createIndex->Create failed!");
				}
	    	} else {
	    		flag = true;
	    		logger.info("createIndex->Index already exist, do nothing!");
	    	}
			
		} catch (Exception e) {
			logger.error("createIndex->Create error!", e);
		}
	    
	    return flag;
    }
	
	
	public void syncData(String index, String type, Integer pageSize, JpaRepository repository) {
		boolean flag = false;
	    try {
	    	logger.info("syncData->Sync mysql data to es '"+index+"' started...");
	    	if(!elasticsearchHelper.isIndexExist(index)) {
				elasticsearchHelper.createIndex(index, type);
			}

			long total = 0;
			int count = 0;
			int currentPage = 0;
			Page<Object> page = null;
			do {
				PageRequest pageRequest  = new PageRequest(currentPage++, pageSize);
				page = repository.findAll(pageRequest);
				total = page.getTotalElements();
				List<Map<String, Object>> maps = new ArrayList<>();
				for(Object obj : page.getContent()) {
					Map<String, Object> map = new HashMap<String, Object>();
					BeanUtils.bean2Map(obj, map);
					maps.add(map);
				}
				flag = elasticsearchHelper.batchInsert(index, type, maps);
				if(flag) count += page.getContent().size();
			} while(page.hasNext());

			logger.info("syncData->"+ total + " records in total, " + count + " records synced in success!");

		} catch (Exception e) {
			logger.error("syncData->Sync data to es '"+index+"' error!", e);
		}
    }
    
	
	
	public boolean deleteIndex(String index) {
		boolean flag = false;
		try {
	    	logger.info("deleteIndex->Delete index '"+index+"' started...");
	    	
	    	if(elasticsearchHelper.isIndexExist(index)) {
	    		flag = elasticsearchHelper.deleteIndex(index);
				if(flag) {
					logger.info("deleteIndex->Delete succeeded!");
				} else {
					logger.info("deleteIndex->Delete failed!");
				}
	    	} else {
	    		flag = true;
	    		logger.info("deleteIndex->Index does't exist, do nothing!");
	    	}
			
		} catch (Exception e) {
			logger.error("deleteIndex->Delete error!", e);
		}
		return flag;
	}
    
	
	
    public boolean insertData(String index, String type, Object data) {
		boolean flag = false;
	    try {
	    	logger.info("insertData->Insert data to index '"+index+"' started...");
	    	
	    	if(elasticsearchHelper.isIndexExist(index)) {
	    		Map<String, Object> map = new HashMap<String, Object>();
				BeanUtils.bean2Map(data, map);
				
				flag = elasticsearchHelper.insertData(index, type,  map.get("id").toString(), map);
				if(flag == true) {
					logger.info("insertData->Insert succeeded!");
				} else {
					logger.info("insertData->Insert failed!");
				}
	    	} else {
	    		logger.info("insertData->Index does't exist, insert failed!");
	    	}
	        
		} catch (Exception e) {
			logger.error("insertData->Insert error!", e);
		}
	    return flag;
    }
    
    
    
    public boolean updateData(String index, String type, Object data) {
    	boolean flag = false;
    	try {
    		logger.info("updateData->Update data to index '"+index+"' started...");
    		
    		if(elasticsearchHelper.isIndexExist(index)) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			BeanUtils.bean2Map(data, map);
    			flag = elasticsearchHelper.updateData(index, type, map.get("id").toString(), map);
    	        if(flag == true) {
    				logger.info("updateData->Update succeeded!");
    			} else {
    				logger.info("updateData->Update failed!");
    			}
    		} else {
    			logger.info("updateData->Index does't exist, update failed!");
    		}
    		
		} catch (Exception e) {
			logger.error("updateData->Update error!", e);
		}
    	return flag;
    }
    
    
    
    public boolean deleteData(String index, String type, String id) {
    	boolean flag = false;
	    try {
	    	logger.info("deleteData->Delete data from index '"+index+"' started...");
	    	
	    	if(elasticsearchHelper.isIndexExist(index)) {
	    		flag = elasticsearchHelper.deleteData(index, type, id);
				if(flag == true) {
					logger.info("deleteData->Delete succeeded!");
				} else {
					logger.info("deleteData->Delete failed!");
				}
	    	} else {
	    		logger.info("deleteData->Index does't exist, delete failed!");
	    	}
			
		} catch (Exception e) {
			logger.error("deleteData->Delete error!", e);
		}
	    return flag;
    }

}
