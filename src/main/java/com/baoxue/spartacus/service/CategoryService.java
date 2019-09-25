package com.baoxue.spartacus.service;

import java.util.List;

import com.baoxue.spartacus.pojo.Category;
import com.baoxue.spartacus.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月18日下午1:53:58
 *
 * @description:
 */
@Service
public class CategoryService {
	
	private Logger logger = LoggerFactory.getLogger(CategoryService.class);
	

	@Autowired
    CategoryRepository categoryRepository;
	
	
	public BaseResp findAll() throws BlogException {
		
		try {
			List<Category> categories = categoryRepository.findAll();
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, categories);
		} catch (Exception e) {
			logger.error("获取文章分类失败！", e);
			return new BaseResp(Globals.CODE_1, Globals.MSG_1);
		}
	}

}
