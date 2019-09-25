package com.baoxue.spartacus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.baoxue.spartacus.security.browser.support.SocialUserInfo;

@Repository
public interface SocialUserInfoRepository  extends JpaRepository<SocialUserInfo, Integer>, JpaSpecificationExecutor<SocialUserInfo> {

	public SocialUserInfo findByProviderIdAndProviderUserId(String providerId, String providerUserId);


}