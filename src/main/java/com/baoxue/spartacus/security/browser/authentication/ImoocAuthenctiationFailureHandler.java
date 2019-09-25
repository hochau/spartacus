package com.baoxue.spartacus.security.browser.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baoxue.spartacus.security.browser.support.SimpleResponse;
import com.baoxue.spartacus.security.core.properties.SecurityProperties;
import com.baoxue.spartacus.security.core.properties.SignInResponseType;

/**
 * 失败处理器，登录失败给页面返回json信息
 * 
 * 跳转 还是 返回 JSON，做成可配
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月6日 下午5:26:06
 */
@Component("imoocAuthenctiationFailureHandler")
public class ImoocAuthenctiationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private SecurityProperties securityProperties;

	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, 
			AuthenticationException exception) throws IOException, ServletException {
		
		logger.info("登录失败");
		
		if (SignInResponseType.JSON.equals(securityProperties.getBrowser().getSignInResponseType())) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(exception.getMessage())));
		} else {
//			super.onAuthenticationFailure(request, response, exception);
			response.sendRedirect(securityProperties.getBrowser().getSignInPage() + "?error=true");
			
		}
		
		
	}

}
