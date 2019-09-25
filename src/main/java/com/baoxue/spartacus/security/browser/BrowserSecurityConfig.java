package com.baoxue.spartacus.security.browser;

import javax.sql.DataSource;

import com.baoxue.spartacus.security.core.code.config.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.context.request.RequestContextListener;

import com.baoxue.spartacus.security.browser.support.MyBCryptPasswordEncoder;
import com.baoxue.spartacus.security.core.properties.SecurityConstants;
import com.baoxue.spartacus.security.core.properties.SecurityProperties;

/**
 * @author zhailiang
 *
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	protected AuthenticationSuccessHandler imoocAuthenticationSuccessHandler;
	
	@Autowired
	protected AuthenticationFailureHandler imoocAuthenticationFailureHandler;
	
	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;
	
	@Autowired
	private SpringSocialConfigurer imoocSocialSecurityConfig;
	
	@Autowired
	private InvalidSessionStrategy invalidSessionStrategy;
	
	@Autowired
	private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;
	
	@Autowired
	private LogoutSuccessHandler logoutSuccessHandler;
	
	
	//这里要配置一个RequestContextListener，否则在MyBCryptPasswordEncoder类中无法直接拿到session
    @Bean
    public ServletListenerRegistrationBean<RequestContextListener> listenerRegistration3() {
        return new ServletListenerRegistrationBean<>(
            new RequestContextListener());
    }
	
    //配置了passwordEncoder后，spring security会自动调用matches()将前端传上来的密码与数据库中加密的密码进行匹配
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new MyBCryptPasswordEncoder();
	}
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
		jdbcTokenRepository.setDataSource(dataSource);
//		jdbcTokenRepository.setCreateTableOnStartup(true);//启动时创建
		return jdbcTokenRepository;
	}
	
	
	@Override
    public void configure(WebSecurity web) throws Exception {
        web
        .ignoring()
	       	.antMatchers(
	       			"/js/**",
	       			"/css/**",
	       			"/img/**",
	       			"/fonts/**",
	       			"/login/**",
	       			"/plugins/**",
	       			"/summernote/**",
	       			"/weather/**",
	       			"/bootstrap-paginator-1.0.2/**",
					"/websocket/**");
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.formLogin()
				.loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL) //如果访问的内容需要登录，则转发到这里
				.loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)
				.successHandler(imoocAuthenticationSuccessHandler)
				.failureHandler(imoocAuthenticationFailureHandler)
				.and()
			.headers().frameOptions().disable()
				.and()
			.apply(validateCodeSecurityConfig) //导入配置
				.and()
			.apply(imoocSocialSecurityConfig) //导入配置
				.and()
			.rememberMe()
				.tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
				.userDetailsService(userDetailsService)
				.and()
			.sessionManagement()
				.invalidSessionStrategy(invalidSessionStrategy)//失效策略
				.maximumSessions(securityProperties.getBrowser().getSession().getMaximumSessions())//并发控制
				.maxSessionsPreventsLogin(securityProperties.getBrowser().getSession().isMaxSessionsPreventsLogin())
				.expiredSessionStrategy(sessionInformationExpiredStrategy)//过期策略
				.and()
				.and()
			.logout()
				.logoutUrl("/signOut") //前端必须用这里定义的路径
//				.logoutSuccessUrl("/signIn.html") //成功退出后跳转的页面，二者配置一个即可
				.logoutSuccessHandler(logoutSuccessHandler) //成功退出后可以做一些自定义的事，比如记录日志、跳转页
				.deleteCookies("JSESSIONID")//同时删除浏览器Cookie
				.and()
			.authorizeRequests()
				.antMatchers(
					securityProperties.getBrowser().getSignInPage(),
					securityProperties.getBrowser().getSignUpUrl(),
					securityProperties.getBrowser().getSignOutUrl(),
					SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
					SecurityConstants.DEFAULT_SESSION_INVALID_URL,
					SecurityConstants.DEFAULT_VALIDATE_CODE_IMAGE_URL,
					SecurityConstants.DEFAULT_GET_MD5_RSA_PUBLIC_KEY_URL)
				.permitAll()
				.anyRequest()
				.authenticated()
				.and()
			.csrf().disable();
	}

}
