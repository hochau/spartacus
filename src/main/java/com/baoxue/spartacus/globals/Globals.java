package com.baoxue.spartacus.globals;

import com.qcloud.cos.model.CannedAccessControlList;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年2月2日下午12:07:20
 *
 * @description: 静态常量
 */
public class Globals {
	
	public static final Integer CODE_0 = 0; //成功code
	public static final String MSG_0 = "success"; //成功msg
	
	public static final Integer CODE_1 = 1; //失败code
	public static final String MSG_1 = "failed"; //失败msg

	public static final Integer CODE_2 = 2; //失败code
	public static final String MSG_2 = "mysql"; //失败msg

	public static final Integer CODE_3 = 3; //失败code
	public static final String MSG_3 = "es"; //失败msg
	
	public static final Integer CODE_NULL = -1; 
	public static final String MSG_NULL = "no record";
	
	public static final Integer CODE_101 = 101; //缺少必填参数
	public static final String MSG_101 = "no params"; //缺少必填参数
	
	public static final Integer CODE_102 = 102; //参数不合法
	public static final String MSG_102 = "invalid params"; //参数不合法
	
	public static final Integer CODE_500 = 500; //失败code
	public static final String MSG_500 = "server inner error"; //失败msg
	
	public static final Integer CODE_501 = 501; //失败code
	public static final String MSG_501 = "unknown error"; //失败msg
	
	//索引名称、类型名称
	public static final String ARTICLE_INDEX_NAME = "article_index";
	public static final String ARTICLE_TYPE_NAME = "article_type";
	
	public static final String COS_RESOURCE_INDEX_NAME = "cos_resource_index";
	public static final String COS_RESOURCE_TYPE_NAME = "cos_resource_type";

	public static final HashMap<Integer, CannedAccessControlList> ACL_MAP;
	static {
		ACL_MAP = new HashMap<Integer, CannedAccessControlList>();
		ACL_MAP.put(1, CannedAccessControlList.Private);
		ACL_MAP.put(2, CannedAccessControlList.PublicRead);
		ACL_MAP.put(3, CannedAccessControlList.PublicReadWrite);
	}

	//SSE
	public static Map<String, SseEmitter> sseEmitters = new HashMap<>();

	//websocket
	public static List<String> webSockets = new ArrayList<>();
}
