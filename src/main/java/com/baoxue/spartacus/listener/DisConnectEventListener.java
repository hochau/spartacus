package com.baoxue.spartacus.listener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class DisConnectEventListener implements ApplicationListener<SessionDisconnectEvent>{
	private static Logger logger = Logger.getLogger(DisConnectEventListener.class);

	public void onApplicationEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		logger.info("DisConnectEventListener监听器 事件类型："+headerAccessor.getCommand().getMessageType());
	}

}
