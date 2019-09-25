package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.utils.JuheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.req.BaseReq;
import com.baoxue.spartacus.controller.resp.BaseResp;

@RestController
@RequestMapping("/common")
public class CommonController {
	
	@Autowired
	private JuheUtils juheUtils;
	
	
	/**
    * 根据城市名称查询天气信息
    *  
    * @author lvchao 2018年5月24日
    * @param baseReq
    * @return
    */
	@RequestMapping("/queryWeather")
	public BaseResp queryWeather(@ModelAttribute BaseReq baseReq) throws BlogException {
	    return juheUtils.queryWeather(baseReq.getCityname(), baseReq.getLon(), baseReq.getLat());
	}

}
