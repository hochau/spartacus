package com.baoxue.spartacus.security.core.properties;

import lombok.Data;

/**
 * @author zhailiang
 *
 */
@Data
public class ImageCodeProperties {
	
	private int length = 4;
	private int expireIn = 60;
	
	private int width = 67;
	private int height = 23;
	
	private String url;
	
}
