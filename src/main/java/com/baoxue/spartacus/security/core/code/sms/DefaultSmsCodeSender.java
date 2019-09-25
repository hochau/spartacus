package com.baoxue.spartacus.security.core.code.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhailiang
 *
 */
@Component("smsCodeSender")
public class DefaultSmsCodeSender implements SmsCodeSender {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void send(String mobile, String code) {
		logger.warn("请配置真实的短信验证码发送器SmsCodeSender");
		logger.info("向手机"+mobile+"发送短信验证码"+code);
	}

}
