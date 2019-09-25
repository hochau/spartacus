package com.baoxue.spartacus.pojo;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HighFrequencyAccess {
	
	private String ip;
	
	private String ipCity;
	
	private Object count; //访问次数
	
	private Map<String, Object> analysis; //分时间段统计
	
	private boolean forbidden = false; //是否被封禁，默认不是
	
}
