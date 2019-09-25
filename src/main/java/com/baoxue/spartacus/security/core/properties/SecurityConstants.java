package com.baoxue.spartacus.security.core.properties;

/**
 * @author zhailiang
 *
 */
public interface SecurityConstants {
	
	/**
	 * 获RSA公钥
	 */
	public static final String DEFAULT_GET_MD5_RSA_PUBLIC_KEY_URL = "/getPublicKey";
	
	/**
	 * 默认的处理验证码的url前缀
	 */
	public static final String DEFAULT_VALIDATE_CODE_URL_PREFIX = "/code";
	
	/**
	 * 图片验证码
	 */
	public static final String DEFAULT_VALIDATE_CODE_IMAGE_URL = "/code/image";
	/**
	 * 短信验证码
	 */
	public static final String DEFAULT_VALIDATE_CODE_SMS_URL = "/code/sms";
	/**
	 * 当请求需要身份认证时，默认跳转的url
	 */
	public static final String DEFAULT_UNAUTHENTICATION_URL = "/authentication/require";
	/**
	 * 默认的用户名密码登录请求处理url
	 */
	public static final String DEFAULT_LOGIN_PROCESSING_URL_FORM = "/authentication/form";
	/**
	 * 默认的手机验证码登录请求处理url
	 */
	public static final String DEFAULT_LOGIN_PROCESSING_URL_MOBILE = "/authentication/mobile";
	/**
	 * 默认注册页面
	 */
	public static final String DEFAULT_SIGNUP_PAGE_URL = "/signUp.html";
	/**
	 * 默认登录页面
	 */
	public static final String DEFAULT_LOGIN_PAGE_URL = "/signIn.html";
	/**
	 * 默认登录成功后的跳转页面
	 */
	public static final String DEFAULT_SIGNIN_SUCCESS_URL = "/index.html";
	/**
	 * 验证图片验证码时，http请求中默认的携带图片验证码信息的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "imageCode";
	/**
	 * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_CODE_SMS = "smsCode";
	/**
	 * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
	 */
	public static final String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";
	/**
	 * session失效默认的跳转地址
	 */
	public static final String DEFAULT_SESSION_INVALID_URL = "/signIn.html";
	/**
	 * openid参数名
	 */
	public static final String DEFAULT_PARAMETER_NAME_OPENID = "openId";
	/**
	 * providerId参数名
	 */
	public static final String DEFAULT_PARAMETER_NAME_PROVIDERID = "providerId";
	/**
	 * 默认的OPENID登录请求处理url
	 */
	public static final String DEFAULT_LOGIN_PROCESSING_URL_OPENID = "/authentication/openid";

	/**
	 * 获取社交用户信息
	 */
	public static final String DEFAULT_GET_SOCIAL_USERINFO_URL = "/getSocialUserInfo";

	/**
	 * 获取管理员用户信息
	 */
	public static final String DEFAULT_GET_ADMIN_USERINFO_URL = "/getAdminUserInfo";
}
