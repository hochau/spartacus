package com.baoxue.spartacus.security.browser.support;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhailiang
 *
 */
@Entity
@Table(name ="tb_socialuserinfo")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column
	private String providerUserId; //qq用openid，weixin用unionid

	@Column
	private String providerId; //qq,weixin

	@Column
	private String nickname;

	@Column
	private String headImg;

	@Column
	private String sex = "男";

	@Column
	private String province;

	@Column
	private String city;

	@Column
	private String country;

	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date refreshTime;

}
