package com.baoxue.spartacus.security.core.properties;

import lombok.Data;

/**
 * @author zhailiang
 *
 */
@Data
public class BrowserProperties {
	
	private SessionProperties session = new SessionProperties();
	
	private String signUpUrl = SecurityConstants.DEFAULT_SIGNUP_PAGE_URL;
	
	private String signInPage = SecurityConstants.DEFAULT_LOGIN_PAGE_URL;
	
	private String signInSuccessUrl = SecurityConstants.DEFAULT_SIGNIN_SUCCESS_URL;
	
	private SignInResponseType signInResponseType = SignInResponseType.JSON;
	
	private int rememberMeSeconds = 3600;
	
	private String signOutUrl;//退出的url,默认为空

}
