package com.baoxue.spartacus.security.browser.logout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baoxue.spartacus.security.browser.support.SimpleResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 实现 LogoutSuccessHandler用来处理退出的逻辑处理，是返回json还是根据用户的配置返回Html
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月14日 下午12:00:44
 */
public class ImoocLogoutSuccessHandler implements LogoutSuccessHandler {
 
	private Logger logger = LoggerFactory.getLogger(getClass());
 
	private String signOutSuccessUrl;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public ImoocLogoutSuccessHandler(String signOutSuccessUrl) {
		this.signOutSuccessUrl = signOutSuccessUrl;
	}
 
 
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		logger.info("退出成功");
		//退出登录的Url如果是默认的空，那么返回的是json数据
		if (StringUtils.isBlank(signOutSuccessUrl)) {
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse("退出成功")));
		} else {
			response.sendRedirect(signOutSuccessUrl);
		}
 
	}
 
}
