package com.baoxue.spartacus.controller.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月19日上午9:16:17
 *
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseReq {
	private Integer currentPage = 0;
	private Integer pageSize = 6;
	
	private Integer status = 0; // 文章/评论状态：0（已发布/待审核）、1（已撤回/审核通过）、2（草稿箱/已删除）
	
	private String searchContent; //搜索内容
	
	private Long articleId; //评论id
	
	//用于获取天气信息
	private String cityname;
	private String cityid;//城市代号
	private String ip;
	private String lon;//经度，如：116.322987
	private String lat;//纬度，如：39.983424
	
	//用于查询浏览记录（today/month/year/all）
	private String flag;
	
	//用于封禁/解封ip
	private boolean forbidden;
	// private String ip; //上面已经有了
	private String ipCity;
	
	// private String ip; //上面已经有了
	private Integer port;
	private String device; //cpu、memory、swap、disk、net
	
	//生成COS目录树的根目录
	private String rootDirPath;

	private String dirPath;
	private Boolean isRecursive;
	
	//新建COS目录
	private String parentDirPath;
	private Long parentId;
	private String newDirName;
	
	//删除COS目录
	private String targetDirPath;
	private String eventId;
	private String subAddress;
	
	//设置COS对象的ACL
	private String key;
	private Integer aclFlag; //1是私有读写，2是公有读私有写，3是公有读写

	//按标签搜索
	private String tag;

	private Integer cosType;
	private String rootPath;

	//多个key，用英文逗号分隔
	private String keysStr;

	private String destDirPath;

	private String newFileName;
}
