package com.baoxue.spartacus.controller.resp;

import java.util.TreeMap;

import com.baoxue.spartacus.pojo.Comment;

/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年3月14日 下午5:42:18
 */
public class CommentResp extends BaseResp {

	private TreeMap<String, Comment> comments;
	
	public CommentResp() {
		super();
	}

	public CommentResp(Integer code, String msg, TreeMap<String, Comment> comments) {
		super(code, msg);
		this.comments = comments;
	}

	public TreeMap<String, Comment> getComments() {
		return comments;
	}

	public void setComments(TreeMap<String, Comment> comments) {
		this.comments = comments;
	}
	

	@Override
	public String toString() {
		return "CommentResp [comments=" + comments + ", getCode()=" + getCode()
				+ ", getMsg()=" + getMsg() + "]";
	}

}
