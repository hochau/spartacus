package com.baoxue.spartacus.pojo;

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
@Table(name ="tb_message")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Message {
	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column
	private Date sendTime;
	
	@Column
	private String message;
	
	
	@Column(nullable = false)
	private Integer userType; //账号类型，0是QQ，1是微信
	
	@Column(nullable = false)
    private String nickname;

	@Column(nullable = false)
    private String headImg;
	
	@Column(nullable = false)
	private String openid; //QQ、微信共用
	
	@Column(nullable = false)
	private String unionid; //微信独有
}
