package com.baoxue.spartacus.utils;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

/**
 * 聚合数据接口访问工具
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2019年1月16日 下午3:25:17
 */
@Component
public class JuheUtils {

	private static Logger logger = Logger.getLogger(JuheUtils.class);
	
	@Autowired
    private BlogProperties blogProperties;
	
	/**
    * 根据城市名称查询天气信息
    *  
    * @author lvchao 2018年5月24日
    * @return
    */
	public BaseResp queryWeather(String cityname, String lon, String lat) throws BlogException {
		try {
	    	HashMap<String,	Object> paramMap = new HashMap<String,	Object>();
	    	paramMap.put("cityname", cityname);
	    	paramMap.put("lon", lon);
	    	paramMap.put("lat", lat);
	    	paramMap.put("dtype", "json");
		    paramMap.put("format", 2);
		    paramMap.put("key", blogProperties.getWeatherKey());
		    
	    	String url = blogProperties.getWeatherUrl();
	    	if(cityname == null || cityname.length() == 0) {
	    		url = blogProperties.getWeatherUrlGps();
	    	}
		    String resp = HttpUtils.doGetMap(url, paramMap);
		    JSONObject jsonObject = JSONObject.parseObject(resp);
	    	if(jsonObject.getInteger("error_code") == 0 && jsonObject.getString("resultcode").equals("200")) {
	    		return new BaseResp(Globals.CODE_0, Globals.MSG_0, jsonObject.get("result"));
	    	}
	    } catch (Exception e) {
	    	logger.error("查询天气失败！", e);
	    }
	    
	    return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
}
