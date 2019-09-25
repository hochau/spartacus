package com.baoxue.spartacus.listener;

import com.baoxue.spartacus.globals.Globals;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class UnSubscribeEventListener implements ApplicationListener<SessionUnsubscribeEvent>{
	private static Logger logger = Logger.getLogger(UnSubscribeEventListener.class);

	public void onApplicationEvent(SessionUnsubscribeEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		if(Globals.webSockets.contains(headerAccessor.getSubscriptionId())) {
			Globals.webSockets.remove(headerAccessor.getSubscriptionId());
		}
		logger.info("UnSubscribeEventListener监听器 事件类型："+headerAccessor.getCommand().getMessageType());
	}

}
