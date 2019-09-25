package com.baoxue.spartacus.controller.resp;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月16日下午4:05:14
 */
public class BaseResp {
	private Integer code = 0;
	private String msg = "success";
	
	private Integer status = 0;
	private Object data;
	
	public BaseResp() {
		super();
	}
	public BaseResp(Integer code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}
	
	public BaseResp(Integer code, String msg, Object data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public BaseResp(Integer code, String msg, Integer status, Object data) {
		super();
		this.code = code;
		this.msg = msg;
		this.status = status;
		this.data = data;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return "BaseResp [code=" + code + ", msg=" + msg + ", status=" + status + ", data=" + data + "]";
	}
	
}
