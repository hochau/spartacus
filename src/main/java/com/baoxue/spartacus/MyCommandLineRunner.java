package com.baoxue.spartacus;

import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.repository.AccessRepository;
import com.baoxue.spartacus.repository.ArticleRepository;
import com.baoxue.spartacus.repository.CosResourceRepository;
import com.baoxue.spartacus.repository.NetIORepository;
import com.baoxue.spartacus.utils.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.baoxue.spartacus.globals.Globals;
import com.baoxue.spartacus.pojo.UserEntity;
import com.baoxue.spartacus.security.browser.support.MyBCryptPasswordEncoder;
import com.baoxue.spartacus.service.UserService;
import com.baoxue.spartacus.task.AsyncTask;

/**
 * 启动后立刻执行的任务
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年5月2日 下午5:26:11
 */
@Component
public class MyCommandLineRunner implements CommandLineRunner{
	
	@Autowired
	AsyncTask task;
	
	@Autowired
	ArticleRepository articleRepository;
	
	@Autowired
	AccessRepository accessRepository;
	
	@Autowired
	NetIORepository netIORepository;
	
	@Autowired
	CosResourceRepository cosResourceRepository;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Autowired
	UserService userService;

	@Autowired
	BlogProperties blogProperties;
	
	PasswordEncoder passwordEncoder = new MyBCryptPasswordEncoder();
	
	
    @Override
    public void run(String... var) throws Exception {
    	/*
    	 * 初始化一个admin用户
    	 */
    	if(!userService.isExist(blogProperties.getAdminUsername())) {
    		UserEntity user = new UserEntity();
    		user.setId(Snowflake.generateId());
    		user.setUsername(blogProperties.getAdminUsername());
    		user.setPassword(passwordEncoder.encode(blogProperties.getAdminPassword()));
    		user.setNickname(blogProperties.getAdminNickname());
    		user.setHeadImg(blogProperties.getAdminHeadImg());
    		user.setRoles("ROLE_ADMIN,ROLE_ACTUATOR");
    		userService.add(user);
    	}
    	
    	task.syncData(Globals.ARTICLE_INDEX_NAME, Globals.ARTICLE_TYPE_NAME, 100, articleRepository);
    	
    	task.syncData(Globals.COS_RESOURCE_INDEX_NAME, Globals.COS_RESOURCE_TYPE_NAME, 500, cosResourceRepository);
    	
//    	System.out.println(accessCountRepository.getTodayScanDetails());
    	
//    	System.out.println(netIoCounterRepository.getTodayNetIoDetails());
    	
//    	System.out.println(netIoCounterRepository.getMonthNetIoDetails());
    	
//    	System.out.println(netIoCounterRepository.getYearNetIoDetails());
    	
//    	Set<String> ips = stringRedisTemplate.keys("*.*.*.*");
//    	System.out.println(ips);
    	
    }
}