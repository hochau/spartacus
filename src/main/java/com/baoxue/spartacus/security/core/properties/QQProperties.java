package com.baoxue.spartacus.security.core.properties;

import org.springframework.boot.autoconfigure.social.SocialProperties;

import lombok.Data;

/**
 * @author zhailiang
 *
 */
@Data
public class QQProperties extends SocialProperties {
	
	private String providerId = "qq";

}
