package com.baoxue.spartacus.controller.req;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月16日下午2:11:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReq {
	private Long id;
	private String title;
	private String author;
	private String labels;
//	private String pictures;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") 
	private Date publishTime;
	
//	private Integer commentNumber = 0;
//	private Integer scanNumber = 0;
	private String cname;
	private String fromWhere; // 原创，摘抄，转载
	private Integer status; // 0表示已发布，1表示存草稿，2表示已撤回，3表示已删除
	private Integer isTop; // 是否置顶，0不是，1是
	private String brief;
	private String content;

}
