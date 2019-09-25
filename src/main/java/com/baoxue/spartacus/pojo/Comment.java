package com.baoxue.spartacus.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name ="tb_comment")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Comment implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@Column
	@JsonSerialize(using= ToStringSerializer.class)
	private Long refId;// 父评论（一级评论）id

	@Column
	@JsonSerialize(using= ToStringSerializer.class)
	private Long frontId;// 子评论（二级评论）链上的上一条

	@Column
	@JsonSerialize(using= ToStringSerializer.class)
	private Long rearId;// 子评论（二级评论）链上的下一条

	@Column(nullable = false)
	@JsonSerialize(using= ToStringSerializer.class)
	private Long articleId; // 对应文章的id

	@Column
    private String articleTitle; //文章标题
	
	@Column(nullable = false, columnDefinition="TEXT")
    private String content;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(nullable = false)
    private Date publishTime;

	@Column(nullable = false, columnDefinition="INT default 1")
    private Integer level = 1; //默认是1即一级评论，2的话表示二级评论
	
	@Transient //不映射到数据库中
	private TreeMap<Long, Comment> subComments; //二级评论列表
	
	@Column(nullable = false, columnDefinition="INT default 0")
    private Integer status = 0; //审核状态，0是待审核，1是通过，2是垃圾评论
	
	@Column
    private String ip;
	
	@Column
    private String ipCity;
	
	//社交用户信息
	@Column
	private String providerId; //账号类型，qq/weixin
	
	@Column
	private String providerUserId; //QQ、微信共用
	
	@Column
    private String nickname;

	@Column
    private String headImg;
	
	
}
