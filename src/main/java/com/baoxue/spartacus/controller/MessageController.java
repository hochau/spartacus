package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.resp.BaseResp;

/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年10月25日 下午5:22:53
 */
@RestController
@RequestMapping("/message")
public class MessageController {
	
	@Autowired
    MessageService messageService;
	
	@RequestMapping("/getRecentMessages")
	public BaseResp getRecentMessages() throws BlogException {
	    return messageService.getRecentMessages();
	}
}
