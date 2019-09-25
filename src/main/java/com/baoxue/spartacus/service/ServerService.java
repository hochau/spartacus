package com.baoxue.spartacus.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.baoxue.spartacus.repository.NetIORepository;
import com.baoxue.spartacus.utils.HttpUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

@Service
public class ServerService {
	
	private static Logger logger = Logger.getLogger(ServerService.class);
	
	@Autowired
    private BlogProperties blogProperties;
	
	@Autowired
    NetIORepository netIORepository;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	
	
	/**
    * 获取集群服务器主机的IP、端口号
    *  
    * @author lvchao 2018年10月25日
    * @return
    * @throws BlogException
    */
	public BaseResp getServerHosts() throws BlogException {
		try {
			Set<String> ips = stringRedisTemplate.keys("*.*.*.*");
			Map<Object, Object> hostMap = new HashMap<Object, Object>();
			for(String ip : ips) {
				hostMap.put(ip, stringRedisTemplate.opsForValue().get(ip));
			}
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, hostMap);
	    } catch (Exception e) {
	    	logger.error("获取服务器主机IP地址、端口号失败！", e);
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	
	/**
	    * 获取服务器设备信息
	    *  
	    * @author lvchao 2018年10月11日
	    * @param ip
	    * @param device 只能取值cpu、memory、swap、disk、net
	    * @return
	    * @throws BlogException
	    */
	public BaseResp getServerStatus(String ip, Integer port, String device) throws BlogException {
		try {
			String url = ip + ":" + port + "/" + device + "?accesstoken=" + blogProperties.getApiAccesstoken();
			String resp = null;
			try {
				resp = HttpUtils.doGetMap("http://"+url, null);
			} catch (Exception e) {
				resp = HttpUtils.doGetMap("https://"+url, null);
			}
			JSONObject jsonObject = JSONObject.parseObject(resp);
			if(jsonObject.getInteger("code") == 0) {
				return new BaseResp(Globals.CODE_0, Globals.MSG_0, jsonObject.get("data"));
			}
	    } catch (Exception e) {
	    	logger.error("获取服务器"+device+"信息失败！", e);
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}


	/**
    * 获取网卡流量统计信息
    *  
    * @author lvchao 2018年10月22日
    * @return
    * @throws BlogException
    */
	public BaseResp getNetIODetails(String ip) throws BlogException {
		try {
			Map<Object, Object> map = new HashMap<Object, Object>();
			Map<Object, Object> dayMap = netIORepository.getTodayNetIoDetails(ip);
			Map<Object, Object> monthMap = netIORepository.getMonthNetIoDetails(ip);
			Map<Object, Object> yearMap = netIORepository.getYearNetIoDetails(ip);
			
			map.put("day", dayMap);
			map.put("month", monthMap);
			map.put("year", yearMap);
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0, map);
	    } catch (Exception e) {
	    	logger.error("获取网卡流量统计详情失败！", e);
	    }
		
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

}
