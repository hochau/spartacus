package com.baoxue.spartacus.security.core.social.qq.connet;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.baoxue.spartacus.security.core.social.qq.api.QQ;
import com.baoxue.spartacus.security.core.social.qq.api.QQUserInfo;

/**
 * 适配器：将QQ API获取到的QQUserInfo对象 适配到 Connection对象
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年8月9日 上午11:16:25
 */
public class QQAdapter implements ApiAdapter<QQ> {

	@Override
	public boolean test(QQ api) {
		return true;
	}

	@Override
	public void setConnectionValues(QQ api, ConnectionValues values) {
		QQUserInfo userInfo = api.getUserInfo();
		
		values.setDisplayName(userInfo.getNickname());
		values.setImageUrl(userInfo.getFigureurl_qq_1());
		values.setProfileUrl(null);
		values.setProviderUserId(userInfo.getOpenId());
	}

	@Override
	public UserProfile fetchUserProfile(QQ api) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateStatus(QQ api, String message) {
		//do noting
	}

}
