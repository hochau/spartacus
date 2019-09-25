package com.baoxue.spartacus.security.core.properties;

import lombok.Data;

@Data
public class OAuth2Properties {
	
	private String jwtSigningKey = "imooc";
	
	private OAuth2ClientProperties[] clients = {};
 
}
