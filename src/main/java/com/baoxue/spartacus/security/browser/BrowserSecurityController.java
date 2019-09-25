package com.baoxue.spartacus.security.browser;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.globals.Globals;
import com.baoxue.spartacus.pojo.UserEntity;
import com.baoxue.spartacus.security.browser.support.SocialUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.security.browser.support.RSAUtils;
import com.baoxue.spartacus.security.browser.support.RedirectHelper;
import com.baoxue.spartacus.security.core.properties.SecurityConstants;
import com.baoxue.spartacus.security.core.properties.SecurityProperties;

/**
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年6月13日 下午5:46:31
 */
@RestController
public class BrowserSecurityController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private RedirectHelper redirectHelper;

	@Autowired
	BlogProperties blogProperties;
	
	
	/**
	 * 当需要身份认证时，未授权的均转发到这里
	 *  
	 * @author lvchao 2018年6月13日
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public void requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest != null) {
			logger.info("引发跳转的请求是:" + savedRequest.getRedirectUrl());
		}
		// 直接跳转到登录页面
//		redirectStrategy.sendRedirect(request, response, securityProperties.getBrowser().getSignInPage()); //有‘在iframe内嵌页中打开’问题
		redirectHelper.jumpOutTheIFrameAndRedirectToLoginPage(request, response, securityProperties.getBrowser().getSignInPage());
	}
	
	@RequestMapping(SecurityConstants.DEFAULT_GET_MD5_RSA_PUBLIC_KEY_URL)
	public BaseResp getRSAPublicKey(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			//生成公钥和私钥
			Map<String,Object> keyMap = RSAUtils.createKey();
			RSAPublicKey publicKey = (RSAPublicKey) keyMap.get("publicKey");
			RSAPrivateKey privateKey = (RSAPrivateKey) keyMap.get("privateKey");
			
			//页面通过模和公钥指数获取公钥对字符串进行加密，注意必须转为16进制
			String modulus = publicKey.getModulus().toString(16); //模
			String public_exponent = publicKey.getPublicExponent().toString(16); //公钥指数
	        String private_exponent = privateKey.getPrivateExponent().toString(); //私钥指数
	        
	        //后台的模和私钥指数不需要转16进制
	        request.getSession().setAttribute("modulus", publicKey.getModulus().toString());
	        request.getSession().setAttribute("private_exponent", private_exponent);
	        
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("modulus", modulus);
	        map.put("public_exponent", public_exponent);
	        return new BaseResp(0, "success", map);
	        
		} catch (Exception e) {
			logger.error("获取公钥出错！", e);
		}
		return new BaseResp(1, "fail", null);
	}

	@GetMapping(SecurityConstants.DEFAULT_GET_ADMIN_USERINFO_URL)
	public BaseResp getAdminUserInfo(Authentication user) {

		try {
			UserEntity userInfo = new UserEntity();
			userInfo.setNickname(blogProperties.getAdminNickname());
			userInfo.setHeadImg(blogProperties.getAdminHeadImg());

			return new BaseResp(Globals.CODE_0, Globals.MSG_0, userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	@GetMapping(SecurityConstants.DEFAULT_GET_SOCIAL_USERINFO_URL)
	public BaseResp getSocialUserInfo(Authentication user) {

		try {
			String jsonText = JSON.toJSONString(user);
			JSONObject connection = JSONObject.parseObject(jsonText).getJSONObject("connection");

			SocialUserInfo socialUserInfo = new SocialUserInfo();
			socialUserInfo.setNickname(connection.getString("displayName"));
			socialUserInfo.setHeadImg(connection.getString("imageUrl"));
			socialUserInfo.setProviderId(connection.getJSONObject("key").getString("providerId"));
			socialUserInfo.setProviderUserId(connection.getJSONObject("key").getString("providerUserId"));

			return new BaseResp(Globals.CODE_0, Globals.MSG_0, socialUserInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
}
