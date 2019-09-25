package com.baoxue.spartacus.listener;

import com.baoxue.spartacus.globals.Globals;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent>{
	private static Logger logger = Logger.getLogger(SubscribeEventListener.class);

	public void onApplicationEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		if(!Globals.webSockets.contains(headerAccessor.getSubscriptionId())) {
			Globals.webSockets.add(headerAccessor.getSubscriptionId());
		}
		logger.info("SubscribeEventListener监听器 事件类型："+headerAccessor.getCommand().getMessageType());
	}

}
