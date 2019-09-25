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
@Table(name ="tb_access_forbid")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccessForbid {
	
	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@Column
	private String ip;
	
	@Column
	private String ipCity;
	
	@Column
	private String forbidType; // 触发哪种超频访问（day/month/year）
	
	@Column
	private Integer dayCount; // 访问次数
	
	@Column
	private Integer monthCount; // 访问次数
	
	@Column
	private Integer yearCount; // 访问次数
	
	@Column
	private Integer totalCount; // 访问次数
	
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date operateTime;


}
