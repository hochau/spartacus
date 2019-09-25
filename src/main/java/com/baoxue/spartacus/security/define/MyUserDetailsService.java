package com.baoxue.spartacus.security.define;

import com.baoxue.spartacus.pojo.UserEntity;
import com.baoxue.spartacus.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * 使用自定义的UserDetailsService，很重要
 * 
 * 处理用户信息获取逻辑：UserDetailsService
 * 
 * 处理用户信息校验逻辑：UserDetails
 * 
 * 处理用户信息加密逻辑：PasswordEncoder
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月6日 下午12:10:12
 */
@Component
public class MyUserDetailsService implements UserDetailsService, SocialUserDetailsService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
    UserService userService;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("表单登录用户名：" + username);
		UserEntity user = userService.findByUsername(username);
        return new SocialUser(username, user.getPassword(), true, true, true, true, AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
	}

	@Override
	public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
		logger.info("社交登录用户ID：" + userId);
		return new SocialUser(userId, "", true, true, true, true, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
	}
	
}
