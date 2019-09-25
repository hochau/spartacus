package com.baoxue.spartacus.service;

import com.baoxue.spartacus.repository.MessageRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

@Service
public class MessageService {

private static Logger logger = Logger.getLogger(MessageService.class);
	
	@Autowired
    MessageRepository messageRepository;

	/**
	 * 获取最近20条信息
	 *  
	 * @author lvchao 2018年10月25日
	 * @return
	 * @throws BlogException
	 */
	public BaseResp getRecentMessages() throws BlogException {
		try {
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0, messageRepository.getRecentMessages());
	    } catch (Exception e) {
	    	logger.error("获取最近的消息数据失败！", e);
	    }
	    
	    return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

}
