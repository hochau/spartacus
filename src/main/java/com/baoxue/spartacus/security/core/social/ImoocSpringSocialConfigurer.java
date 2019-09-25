package com.baoxue.spartacus.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义ImoocSpringSocialConfigurer，覆盖postProcess方法，然后即可使用配置的QQ登录地址（即QQ提供商的回调地址）
 * 
 * SocialAuthenticationFilter中QQ默认的访问地址前缀是/auth
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月9日 下午4:43:38
 */
public class ImoocSpringSocialConfigurer extends SpringSocialConfigurer {
	
	@Getter @Setter
	private String filterProcessesUrl;
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		return (T) filter;
	}

}
