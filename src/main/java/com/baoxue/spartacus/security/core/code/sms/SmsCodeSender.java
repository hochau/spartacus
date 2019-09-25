package com.baoxue.spartacus.security.core.code.sms;

/**
 * @author zhailiang
 *
 */
public interface SmsCodeSender {

	public void send(String mobile, String code);

}
