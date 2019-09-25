package com.baoxue.spartacus.service;

import com.baoxue.spartacus.pojo.Article;
import com.baoxue.spartacus.pojo.PageEntity;
import com.baoxue.spartacus.repository.ArticleRepository;
import com.baoxue.spartacus.repository.ElasticsearchHelper;
import com.baoxue.spartacus.task.AsyncTask;
import com.baoxue.spartacus.utils.CommonUtils;
import com.baoxue.spartacus.utils.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.baoxue.spartacus.controller.req.ArticleReq;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月16日下午3:56:34
 */
@Service
public class ArticleService {
	
	private Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Lazy
	@Autowired
    AsyncTask task;
	
	@Autowired
    ArticleRepository articleRepository;
	
	@Autowired
    ElasticsearchHelper elasticsearchHelper;

	@Autowired
	ElasticsearchService elasticsearchService;

	/**
	 * 数据同步到ES
	 * @return
	 */
	public BaseResp syncData() {
		try {
			elasticsearchService.syncData(Globals.ARTICLE_INDEX_NAME, Globals.ARTICLE_TYPE_NAME, 100, articleRepository);
		} catch (Exception e) {
			logger.error("syncData->数据同步异常！", e);
			return new BaseResp(Globals.CODE_1, Globals.MSG_1);
		}

		return new BaseResp(Globals.CODE_0, Globals.MSG_0);
	}

	/**
	 * 
	 * @author lvchao
	 * @createtime 2018年1月19日 下午6:16:54
	 * @description 根据id获取文章简介
	 *
	 * @param id
	 * @return
	 * @throws BlogException
	 */
	public BaseResp getArticle(Long id) throws BlogException {
		try {
			Article article = articleRepository.getOne(id); // 不使用懒加载
	        return new BaseResp(Globals.CODE_0, Globals.MSG_0, article);
		} catch (Exception e) {
			logger.info("根据id获取文章失败：" + id, e);
		}
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2018年1月18日 下午2:10:53
	 * @description 分页返回文章基本信息列表
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws BlogException
	 */
	public BaseResp findByStatus(Integer status, Integer currentPage, Integer pageSize) throws BlogException {
		try {
	        Order publishTimeOrder = new Order(Direction.DESC, "publishTime");
	        Sort sort = new Sort(publishTimeOrder);
	        PageRequest pageRequest  = new PageRequest(currentPage-1, pageSize, sort);
	        Page<Article> page = articleRepository.findByStatus(status, pageRequest);
	        PageEntity pageEntity = new PageEntity();
	        pageEntity.setCurrentPage(page.getNumber());
	        pageEntity.setPageSize(pageSize);
	        pageEntity.setTotalPages(page.getTotalPages());
	        pageEntity.setTotal((int) page.getTotalElements());
	        pageEntity.setRecords(page.getContent());
	        return new BaseResp(Globals.CODE_0, Globals.MSG_0, status, pageEntity);
		} catch (Exception e) {
			logger.info("查询失败！", e);
		}
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	
	/**
	 * 基于ES的搜索引擎根据任意内容搜索文章
	 *  
	 * @author lvchao 2018年5月3日
	 * @return
	 * @throws BlogException
	 */
	public BaseResp search(String searchContent, Integer status, int currentPage, int pageSize) throws BlogException {
		String highlightFields = "title,author,labels,brief";
		String matchFields = "title,author,labels,brief";
		try {
			Map<String, Object> mustMatchs = new HashMap<>();
			mustMatchs.put("status", status);
			PageEntity pageEntity = elasticsearchHelper.searchPageData(Globals.ARTICLE_INDEX_NAME, Globals.ARTICLE_TYPE_NAME, currentPage-1, pageSize, 0, 0, null, "publishTime", highlightFields, mustMatchs, searchContent, matchFields);
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, status, pageEntity);
		} catch (Exception e) {
			logger.info("搜索失败！", e);
		}
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	
	/**
    *
    * @author lvchao
    * @createtime 2018年1月18日 下午2:10:31
    * @description 发布一条文章记录
    *
    * @param articleReq
    * @return
    * @throws BlogException
    */
	@Transactional
	public BaseResp release(ArticleReq articleReq) throws BlogException {
		Article article = new Article(articleReq);
		article.setId(Snowflake.generateId());
		article.setStatus(0);
		try {
			logger.info("开始发布文章: " + article.getTitle());
			articleRepository.saveAndFlush(article);
			task.insertEsData(Globals.ARTICLE_INDEX_NAME, Globals.ARTICLE_TYPE_NAME, new Article(article));
			logger.info("发布成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.info("发布失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	
	/**
    *
    * @author lvchao
    * @createtime 2018年1月18日 下午2:10:31
    * @description 存草稿一条文章记录
    *
    * @param articleReq
    * @return
    * @throws BlogException
    */
	@Transactional
    public BaseResp draft(ArticleReq articleReq) throws BlogException {
		Article article = new Article(articleReq);
		article.setId(Snowflake.generateId());
		article.setStatus(1);
		try {
			logger.info("开始保存草稿: " + article.getTitle());
			articleRepository.saveAndFlush(article);
			task.updateEsData(Globals.ARTICLE_INDEX_NAME, Globals.ARTICLE_TYPE_NAME, new Article(article));
			logger.info("保存成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.info("保存失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
    }

	
	/**
    * 
    * @author lvchao
    * @createtime 2018年2月2日 下午1:49:22
    * 
    * @description 更新文章，发生异常时整个事务回滚
    * @return
    * @throws BlogException
    */
    @Transactional
    public BaseResp update(ArticleReq req) throws BlogException {
    	Article article = articleRepository.findOne(req.getId()); //findOne方法才能启动懒加载, getOne不行
    	this.setValues(article, req);
		try {
			logger.info("开始更新文章: " + article.getTitle());
			articleRepository.saveAndFlush(article);
			task.updateEsData(Globals.ARTICLE_INDEX_NAME, Globals.ARTICLE_TYPE_NAME, new Article(article));
			logger.info("更新成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.info("更新失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
    }
    
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 把请求对象各个字段的值赋给通过懒加载得到的持久化对象
     *  
     * @author lvchao 2018年4月3日 下午5:02:17
     * @param article
     * @param req
     */
    public void setValues(Article article, ArticleReq req) {
		if(!CommonUtils.isNull(req.getTitle())) {
			article.setTitle(req.getTitle());
		}
		if(!CommonUtils.isNull(req.getAuthor())) {
			article.setAuthor(req.getAuthor());
		}
		if(!CommonUtils.isNull(req.getLabels())) {
			article.setLabels(req.getLabels());
		}
		if(!CommonUtils.isNull(req.getPublishTime())) {
			article.setPublishTime(req.getPublishTime());
		}
		if(!CommonUtils.isNull(req.getCname())) {
			article.setCname(req.getCname());
		}
		if(!CommonUtils.isNull(req.getFromWhere())) {
			article.setFromWhere(req.getFromWhere());
		}
		if(!CommonUtils.isNull(req.getStatus())) {
			article.setStatus(req.getStatus());
		}
		if(!CommonUtils.isNull(req.getIsTop())) {
			article.setIsTop(req.getIsTop());
		}
		if(!CommonUtils.isNull(req.getContent())) {
			article.setContent(req.getContent().trim());
		}
		if(!CommonUtils.isNull(req.getContent())) {
			article.setPictures(CommonUtils.getPictures(req.getContent().trim()));
		}
		if(!CommonUtils.isNull(req.getBrief())) {
			article.setBrief(req.getBrief());
		} else if(!CommonUtils.isNull(req.getContent())) {
			article.setBrief(CommonUtils.getBrief(req.getContent().trim()));
		}
		if(!CommonUtils.isNull(req.getPublishTime())) {
			article.setYear(CommonUtils.getDateString("yyyy", req.getPublishTime()));
			article.setMonthDay(CommonUtils.getDateString("MM.dd", req.getPublishTime()));
		}
    }

}
