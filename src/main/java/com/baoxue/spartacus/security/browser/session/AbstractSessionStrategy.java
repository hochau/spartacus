package com.baoxue.spartacus.security.browser.session;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baoxue.spartacus.security.browser.support.RedirectHelper;
import com.baoxue.spartacus.security.browser.support.SimpleResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhailiang
 *
 */
public class AbstractSessionStrategy {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * 跳转的url
	 */
	private String invalidSessionUrl;
	
	/**
	 * 跳转前是否创建新的session
	 */
	private boolean createNewSession = true;
	
	/**
	 * 重定向策略
	 */
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	private RedirectHelper redirectHelper;
	

	public AbstractSessionStrategy(String invalidSessionUrl) {
		Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
		this.invalidSessionUrl = invalidSessionUrl;
	}

	public void setCreateNewSession(boolean createNewSession) {
		this.createNewSession = createNewSession;
	}
	
	protected boolean isConcurrency() {
		return false;
	}
	
	protected void onSessionInvalid(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (createNewSession) {
			request.getSession();
		}

		String sourceUrl = request.getRequestURI();
		if (StringUtils.endsWithIgnoreCase(sourceUrl, ".html")) {
			String targetUrl = invalidSessionUrl;
			logger.info("session已失效，跳转到：" + targetUrl);
			
//			redirectStrategy.sendRedirect(request, response, targetUrl);
			redirectHelper.jumpOutTheIFrameAndRedirectToLoginPage(request, response, targetUrl);
			
	
		} else {
			String message = "session已失效";
			if(isConcurrency()){
				message = message + "，有可能是并发登录导致的！";
			} else {
				message = message + "，已过期，请尝试重新登录！";
			}
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(message)));
		}
		
	}

}
