package com.baoxue.spartacus.controller.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WbInMessage {
	
	private String from;
	
	private String to;
	
	private Object payload;

	private Long timestamp;

}
