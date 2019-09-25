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
@Table(name ="tb_access")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Access {
	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;
	
	@Column
	private String url;
	
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date accessTime;
	
	@Column
	private String cookieFlag; //客户端浏览器当日唯一cookie标识符，由服务端设置
	
	@Column
	private String ip;
	
	@Column
	private String ipCity;
	
	@Column
	private String gps; //全球统一经、纬度（WGS84），经、纬度中间用英文逗号分隔
	
	@Column
	private String gpsAddress;
		
}
