package com.baoxue.spartacus.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.baoxue.spartacus.pojo.Access;
import com.baoxue.spartacus.pojo.AccessForbid;
import com.baoxue.spartacus.pojo.HighFrequencyAccess;
import com.baoxue.spartacus.pojo.PageEntity;
import com.baoxue.spartacus.repository.AccessForbidRepository;
import com.baoxue.spartacus.repository.AccessRepository;
import com.baoxue.spartacus.utils.Snowflake;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

@Service
public class AccessService {
	
	private static Logger logger = Logger.getLogger(AccessService.class);
	
	@Autowired
    AccessRepository accessRepository;
	
	@Autowired
    AccessForbidRepository accessForbidRepository;
	
	
	/**
    * 获取浏览量、访客、IP、评论等统计信息
    *  
    * @author lvchao 2018年5月24日
    * @return
    * @throws BlogException
    */
	public BaseResp getStatistics() throws BlogException {
	    try {
	    	HashMap<String, Object> statistics = new HashMap<String, Object>();
	    	statistics.put("totalScan", accessRepository.getTotalScanCount());
	    	statistics.put("todayScan", accessRepository.getTodayScanCount());
	    	statistics.put("todayScanDetails", accessRepository.getTodayScanDetails());
	    	statistics.put("yestodayScan", accessRepository.getYestodayScanCount());
	    	statistics.put("todayScanDetails", accessRepository.getTodayScanDetails());
	    	statistics.put("monthScan", accessRepository.getMonthScanCount());
	    	statistics.put("lastMonthScan", accessRepository.getLastMonthScanCount());
	    	statistics.put("monthScanDetails", accessRepository.getMonthScanDetails());
	    	statistics.put("yearScan", accessRepository.getYearScanCount());
	    	statistics.put("lastYearScan", accessRepository.getLastYearScanCount());
	    	statistics.put("yearScanDetails", accessRepository.getYearScanDetails());
	    	
	    	statistics.put("totalVisitor", accessRepository.getTotalVisitorCount());
	    	statistics.put("todayVisitor", accessRepository.getTodayVisitorCount());
	    	statistics.put("yestodayVisitor", accessRepository.getYestodayVisitorCount());
	    	statistics.put("todayVisitorDetails", accessRepository.getTodayVisitorDetails());
	    	statistics.put("monthVisitor", accessRepository.getMonthVisitorCount());
	    	statistics.put("lastMonthVisitor", accessRepository.getLastMonthVisitorCount());
	    	statistics.put("monthVisitorDetails", accessRepository.getMonthVisitorDetails());
	    	statistics.put("yearVisitor", accessRepository.getYearVisitorCount());
	    	statistics.put("lastYearVisitor", accessRepository.getLastYearVisitorCount());
	    	statistics.put("yearVisitorDetails", accessRepository.getYearVisitorDetails());
	    	
	    	statistics.put("totalIP", accessRepository.getTotalIPCount());
	    	statistics.put("todayIP", accessRepository.getTodayIPCount());
	    	statistics.put("yestodayIP", accessRepository.getYestodayIPCount());
	    	statistics.put("todayIPDetails", accessRepository.getTodayIPDetails());
	    	statistics.put("monthIP", accessRepository.getMonthIPCount());
	    	statistics.put("lastMonthIP", accessRepository.getLastMonthIPCount());
	    	statistics.put("monthIPDetails", accessRepository.getMonthIPDetails());
	    	statistics.put("yearIP", accessRepository.getYearIPCount());
	    	statistics.put("lastYearIP", accessRepository.getLastYearIPCount());
	    	statistics.put("yearIPDetails", accessRepository.getYearIPDetails());
	    	
	    	statistics.put("totalComment", accessRepository.getTotalCommentCount());
	    	statistics.put("todayComment", accessRepository.getTodayCommentCount());
	    	statistics.put("yestodayComment", accessRepository.getYestodayCommentCount());
	    	statistics.put("todayCommentDetails", accessRepository.getTodayCommentDetails());
	    	statistics.put("monthComment", accessRepository.getMonthCommentCount());
	    	statistics.put("lastMonthComment", accessRepository.getLastMonthCommentCount());
	    	statistics.put("monthCommentDetails", accessRepository.getMonthCommentDetails());
	    	statistics.put("yearComment", accessRepository.getYearCommentCount());
	    	statistics.put("lastYearComment", accessRepository.getLastYearCommentCount());
	    	statistics.put("yearCommentDetails", accessRepository.getYearCommentDetails());
	    	
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0, statistics);
	    } catch (Exception e) {
	    	logger.error("统计信息获取失败！", e);
	    }
	    
	    return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
    * 分页获取访问记录
    *  
    * @author lvchao 2018年9月13日
    * @param flag day/month/year/all
    * @param currentPage
    * @param pageSize
    * @return
    * @throws BlogException
    */
	public BaseResp getScanRecords(String flag, Integer currentPage, Integer pageSize) throws BlogException {
		try {
			PageEntity pageEntity = new PageEntity();
	        pageEntity.setCurrentPage(currentPage);
	        pageEntity.setPageSize(pageSize);
	        List<Access> records = null;
	        Integer total = 0;
			if(flag.equals("day")) {
				records = accessRepository.getTodayScan(currentPage-1, pageSize);
				total = accessRepository.getTodayScanCount();
			} else if(flag.equals("month")) {
				records = accessRepository.getMonthScan(currentPage-1, pageSize);
				total = accessRepository.getMonthScanCount();
			} else if(flag.equals("year")) {
				records = accessRepository.getYearScan(currentPage-1, pageSize);
				total = accessRepository.getYearScanCount();
			} else if(flag.equals("total")) {
				records = accessRepository.getTotalScan(currentPage-1, pageSize);
				total = accessRepository.getTotalScanCount();
			}
			pageEntity.setTotal(total);
	        pageEntity.setTotalPages((total + pageSize - 1) / pageSize);
	        pageEntity.setRecords(records);
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageEntity);
	    } catch (Exception e) {
	    	logger.error("获取浏览记录失败！", e);
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	
	/**
    * 分页获取高频访问IP的信息
    *  
    * @author lvchao 2018年9月14日
    * @param flag
    * @param currentPage
    * @param pageSize
    * @return
    * @throws BlogException
    */
	public BaseResp getHighFrequencyAccesses(String flag, Integer currentPage, Integer pageSize) throws BlogException {
		try {
			PageEntity pageEntity = new PageEntity();
	        pageEntity.setCurrentPage(currentPage);
	        pageEntity.setPageSize(pageSize);
	        List<HighFrequencyAccess> records = null;
	        Integer total = 0;
			if(flag.equals("day")) {
				records = accessRepository.getTodayHighFrequencyAccessess(currentPage-1, pageSize);
				total = accessRepository.getTodayHighFrequencyAccessessCount();
			} else if(flag.equals("month")) {
				records = accessRepository.getMonthHighFrequencyAccessess(currentPage-1, pageSize);
				total = accessRepository.getMonthHighFrequencyAccessessCount();
			} else if(flag.equals("year")) {
				records = accessRepository.getYearHighFrequencyAccessess(currentPage-1, pageSize);
				total = accessRepository.getYearHighFrequencyAccessessCount();
			} else if(flag.equals("total")) {
				records = accessRepository.getTotalHighFrequencyAccessess(currentPage-1, pageSize);
				total = accessRepository.getTotalHighFrequencyAccessessCount();
			}
			pageEntity.setTotal(total);
	        pageEntity.setTotalPages((total + pageSize - 1) / pageSize);
	        pageEntity.setRecords(records);
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageEntity);
	    } catch (Exception e) {
	    	logger.error("获取高频访问IP列表失败！", e);
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
    * 封禁IP
    *  
    * @author lvchao 2018年9月21日
    * @return
    * @throws BlogException
    */
	@Transactional
	public BaseResp forbid(AccessForbid forbid) throws BlogException {
		try {
			forbid.setId(Snowflake.generateId());
			forbid.setDayCount(accessRepository.getTodayAccessCountByIp(forbid.getIp()));
			forbid.setMonthCount(accessRepository.getMonthAccessCountByIp(forbid.getIp()));
			forbid.setYearCount(accessRepository.getYearAccessCountByIp(forbid.getIp()));
			forbid.setTotalCount(accessRepository.getTotalAccessCountByIp(forbid.getIp()));
			forbid.setOperateTime(new Date());
			accessForbidRepository.save(forbid);
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0);
	    } catch (Exception e) {
	    	logger.error("封禁失败！", e);
	    	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
    * 解封IP
    *  
    * @author lvchao 2018年11月23日
    * @param ip
    * @return
    * @throws BlogException
    */
	@Transactional
	public BaseResp unForbid(String ip) throws BlogException {
		try {
			accessForbidRepository.deleteByIp(ip);
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0);
	    } catch (Exception e) {
	    	logger.error("解封失败！", e);
	    	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	
	/**
    * 分页获取封禁列表
    *  
    * @author lvchao 2018年11月23日
    * @param currentPage
    * @param pageSize
    * @return
    * @throws BlogException
    */
	public BaseResp getForbids(String flag, Integer currentPage, Integer pageSize) throws BlogException {
		try {
			logger.info("getForbids->开始获取访问黑名单...");
			Order operateTimeOrder = new Order(Direction.DESC, "operateTime");
	        Sort sort = new Sort(operateTimeOrder);
	        PageRequest pageRequest  = new PageRequest(currentPage-1, pageSize, sort);
	        Page<AccessForbid> page = accessForbidRepository.findAll(pageRequest);
	        HashMap<Object, Object> pageMap = new HashMap<Object, Object>();
	        pageMap.put("forbids", page.getContent());
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", page.getTotalPages());
	        pageMap.put("total", page.getTotalElements());
	        logger.info("getForbids->获取成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageMap);
		} catch (Exception e) {
			logger.error("getForbids->获取失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}


	/**
    * 批量解封
    *  
    * @author lvchao 2018年11月23日
    * @param jsonParams
    * @return
    * @throws BlogException
    */
	@Transactional
	public BaseResp batchUnForbid(String jsonParams) throws BlogException {
		try {
			logger.info("batchUnForbid->开始批量解封访问黑名单...");
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				String ip = jsonArray.getJSONObject(i).getString("ip");
				accessForbidRepository.deleteByIp(ip);
			}
			logger.info("batchUnForbid->批量解封成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchUnForbid->批量解封失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
}
