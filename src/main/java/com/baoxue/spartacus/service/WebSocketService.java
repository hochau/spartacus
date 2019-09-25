package com.baoxue.spartacus.service;

import com.baoxue.spartacus.controller.websocket.WbOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


/**
 * 功能描述：基于简单消息模板，推送消息
 *
 * @Author C
 * @Date 2019/8/27 2:08
 **/
@Service
public class WebSocketService {

	
	@Autowired
	private SimpMessagingTemplate template;
	
	public void sendMessage(String destination, WbOutMessage message) {
	
		template.convertAndSend(destination, message);
	}
	
	
}
