package com.baoxue.spartacus.security.core.social.qq.config;

import com.baoxue.spartacus.security.core.properties.QQProperties;
import com.baoxue.spartacus.security.core.social.qq.connet.QQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;

import com.baoxue.spartacus.security.core.properties.SecurityProperties;

/**
 * 配置QQ链接工厂
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月9日 下午3:01:37
 */
@Configuration
@ConditionalOnProperty(prefix = "imooc.security.social.qq", name = "app-id")
public class QQAutoConfig extends SocialAutoConfigurerAdapter {

	@Autowired
	private SecurityProperties securityProperties;

	
	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		QQProperties qqConfig = securityProperties.getSocial().getQq();
		return new QQConnectionFactory(qqConfig.getProviderId(), qqConfig.getAppId(), qqConfig.getAppSecret());
	}

}
