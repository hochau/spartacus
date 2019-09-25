package com.baoxue.spartacus.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name ="tb_comment_forbid")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentForbid implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@Column
	private Integer forbidType = 0; //0表示禁的是id，1表示禁的是ip
	
	//ip地址信息
	@Column
	private String ip;
	
	@Column
	private String ipCity;
	
	//社交信息
	@Column
	private String providerId; //账号类型，qq/weixin
	
	@Column
	private String providerUserId; //QQ、微信共用
	
	@Column
    private String nickname;

	@Column
    private String headImg;
	
	
	@Column(nullable = false, columnDefinition="TEXT")
    private String reason;

	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date operateTime;
	
}
