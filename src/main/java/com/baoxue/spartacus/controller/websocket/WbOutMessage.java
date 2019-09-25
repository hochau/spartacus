package com.baoxue.spartacus.controller.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WbOutMessage {

	private String from;

	private String to;
	
	private Object payload;
	
	private Long timestamp;

	public WbOutMessage(WbInMessage message) {
		this.from = message.getTo();
		this.to = message.getFrom();
		this.payload = message.getPayload();
		this.timestamp = System.currentTimeMillis();
	}

}
