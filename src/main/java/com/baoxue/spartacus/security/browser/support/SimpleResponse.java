package com.baoxue.spartacus.security.browser.support;

/**
 * @author zhailiang
 *
 */
public class SimpleResponse {
	
	private Object content;
	
	
	public SimpleResponse(Object content){
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}
	
}
