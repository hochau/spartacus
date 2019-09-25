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
@Table(name ="tb_mission")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Mission {
	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@Column
	private Integer missionId;
	
	@Column
	private String missionName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column
	private Date exeTime;
	
	@Column(columnDefinition="INT default 1")
	private Integer exeTimes = 1;
	
	@Column(columnDefinition="INT default 0")
	private Integer status = 0; //0 待执行，1 已执行， 2 已取消， 3 失败了

}
