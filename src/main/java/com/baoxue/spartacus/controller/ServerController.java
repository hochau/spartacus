package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.req.BaseReq;
import com.baoxue.spartacus.controller.resp.BaseResp;

/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年10月25日 下午6:19:03
 */
@RestController
@RequestMapping("/server")
public class ServerController {
	
	@Autowired
    ServerService serverService;

	@RequestMapping("/getServerHosts")
	public BaseResp getServerHosts() throws BlogException {
	    return serverService.getServerHosts();
	}
	
	@RequestMapping("/getServerStatus")
	public BaseResp getServerStatus(@ModelAttribute BaseReq baseReq) throws BlogException {
	    return serverService.getServerStatus(baseReq.getIp(), baseReq.getPort(), baseReq.getDevice());
	}
	
	@RequestMapping("/getNetIoCountDetails")
	public BaseResp getNetIoCountDetails(@ModelAttribute BaseReq baseReq) throws BlogException {
	    return serverService.getNetIODetails(baseReq.getIp());
	}
	
}
