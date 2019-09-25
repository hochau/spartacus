//package com.baoxue.blog.controller;
//
//import com.baoxue.blog.controller.websocket.WbInMessage;
//import com.baoxue.blog.controller.websocket.WbOutMessage;
//import com.baoxue.blog.service.WebSocketService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.stereotype.Controller;
//
//
//@Controller
//public class WebSocketController {
//
//
//	@Autowired
//	private WebSocketService ws;
//
//
//	@MessageMapping("/v2/chat")
//	public void gameInfo(WbInMessage message) throws InterruptedException{
//
//		ws.sendMessage("/topic/game_rank", new WbOutMessage(message));
//	}
//
//
//
//
//}
//
//
//
