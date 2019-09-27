package com.baoxue.spartacus.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.baoxue.spartacus.utils.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import com.baoxue.spartacus.controller.req.ArticleReq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月9日下午4:17:41
 */
@Entity
@Table(name ="tb_article")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler"})
public class Article implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
//	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@Column
	private String title;
	
	@Column
	private String author;
	
	@Column
	private String labels;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column
	private Date publishTime;
	
	@Column(columnDefinition="INT default 0")
	private Integer commentNumber = 0;
	
	@Column(columnDefinition="INT default 0")
	private Integer scanNumber = 0;
	
	@Column
	private String cname;
	
	@Column
	private String fromWhere; // 原创，摘抄，转载
	
	@Column(columnDefinition="INT default 0")
	private Integer status = 0; // 0表示已发布，1表示存草稿，2表示已撤回
	
	@Column(columnDefinition="INT default 0")
	private Integer isTop = 0; // 是否置顶，0不是，1是
	
	@Column(columnDefinition="TEXT")
	private String brief;
	
	@Column
	private String year;
	
	@Column
	private String monthDay;

	@Column(columnDefinition="TEXT")
	private String pictures; // 插图
	
	@Column(columnDefinition="TEXT")
	private String content;
	
	
	public Article(Article article) {
		this.id = article.getId();
		this.title = article.getTitle();
		this.author = article.getAuthor();
		this.labels = article.getLabels();
		this.publishTime = article.getPublishTime();
		this.commentNumber = article.getCommentNumber();
		this.scanNumber = article.getScanNumber();
		this.cname = article.getCname();
		this.fromWhere = article.getFromWhere();
		this.status = article.getStatus();
		this.isTop = article.getIsTop();
		this.brief = article.getBrief();
		this.year = article.getYear();
		this.monthDay = article.getMonthDay();
		this.pictures = article.getPictures();
		this.content = "null";
	}
	
	
	public Article(ArticleReq req) {
		this.id = req.getId();
		this.title = req.getTitle();
		this.author = req.getAuthor();
		this.labels = req.getLabels();
		this.publishTime = req.getPublishTime();
		this.cname = req.getCname();
		this.fromWhere = req.getFromWhere();
		this.status = req.getStatus();
		this.content = req.getContent().trim();
		
		if(!CommonUtils.isNull(req.getContent())) {
			this.pictures = CommonUtils.getPictures(req.getContent().trim());
		}
		
		if(!CommonUtils.isNull(req.getContent())) {
			this.pictures = CommonUtils.getPictures(req.getContent().trim());
		}
		
		if(!CommonUtils.isNull(req.getBrief())) {
			this.brief = req.getBrief();
		} else if(!CommonUtils.isNull(req.getContent())) {
			this.brief = CommonUtils.getBrief(req.getContent().trim());
		}
		
		this.year = CommonUtils.getDateString("yyyy", req.getPublishTime());
		this.monthDay = CommonUtils.getDateString("MM.dd", req.getPublishTime());
	}
	
	
	public Article(Long id, String title, String author, String labels, Date publishTime, Integer commentNumber,
			Integer scanNumber, String cname, String fromWhere, Integer status, Integer isTop, String brief,
			String year, String monthDay, String pictures) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.labels = labels;
		this.publishTime = publishTime;
		this.commentNumber = commentNumber;
		this.scanNumber = scanNumber;
		this.cname = cname;
		this.fromWhere = fromWhere;
		this.status = status;
		this.isTop = isTop;
		this.brief = brief;
		this.year = year;
		this.monthDay = monthDay;
		this.pictures = pictures;
	}
	
}
