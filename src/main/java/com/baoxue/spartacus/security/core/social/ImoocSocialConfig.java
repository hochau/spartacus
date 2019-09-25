package com.baoxue.spartacus.security.core.social;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;

import com.baoxue.spartacus.security.core.properties.SecurityProperties;

/**
 * 这里出现了一个问题：扫码注册后，再次扫码还是要跳转的注册页面
 * 
 * 断点调试后发现，一定要加 @Order(1)，否则SocialAuthenticationProvider中的toUserId会
 * 
 * 默认调用用InMemoryUsersConnectionRepository，而不使用这里实例化的的JdbcUsersConnectionRepository
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月10日 下午6:06:26
 */
@Order(1)
@Configuration
@EnableSocial
public class ImoocSocialConfig extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired(required = false) //并不一定会提供
	private ConnectionSignUp connectionSignUp;
	
	
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
		repository.setTablePrefix("tb_");
		if(connectionSignUp != null) {
			repository.setConnectionSignUp(connectionSignUp);
		}
		return repository;
	}
	

	/**
	 * 实例化一个自定义的过滤器配置（ImoocSpringSocialConfigurer）
	 *  
	 * @author lvchao 2018年8月9日
	 * @return
	 */
	@Bean
	public SpringSocialConfigurer imoocSocialSecurityConfig() {
		ImoocSpringSocialConfigurer configurer = new ImoocSpringSocialConfigurer();
		configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
		configurer.setFilterProcessesUrl(securityProperties.getSocial().getFilterProcessesUrl());
		return configurer;
	}
	

	/**
	 * 这个工具类解决两个问题：
	 * 	1、在注册过程中如何拿到SpringSocial的信息
	 * 	2、注册完了如何把业务系统的用户的userId再传给SpringSocial
	 *   
	 * @author lvchao 2018年8月10日
	 * @param connectionFactoryLocator
	 * @return
	 */
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				getUsersConnectionRepository(connectionFactoryLocator)) {
		};
	}
	
}
