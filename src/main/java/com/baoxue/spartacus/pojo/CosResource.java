package com.baoxue.spartacus.pojo;

import java.util.Date;

import javax.persistence.*;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 腾讯云COS资源映射表
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2019年1月31日 下午5:58:52
 */
@Entity
@Table(name ="tb_cos_resource")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CosResource {
	@Id
//	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;

	//构建目录树时用到
	@Column
//	@JsonSerialize(using= ToStringSerializer.class)
	private Long parentId;
	
	@Column
	private String fileName;

	// COS实体对象相对根目录的路径，比如 image/工作/logo.png
	// key是是数据库关键字
	@Column(name = "_key")
	private String key;

	// 对象公网访问路径，如 https://wwww.xxx.com/image/logo.png
	@Transient
	@JSONField(name="url")
	private String url;

	// 标签，以英文逗号','分隔
	@Column
	private String tags;
	
	// 资源类型，image/png
	@Column
	private String contentType;

	// cos类型，0表示实际资源对象，1表示虚拟目录
	@Column
	private Integer cosType = 0;
	
	// 状态，0是可用，1是不可用（在废弃池中）
	@Column
	private Integer status = 0;
	
	@Column
	private String bucketName;
	
	@Column
	private String region;

	@Column
	private String rootPath;
	
	// 初次上传时间
	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastModified;
	
	// 注意：当在客户端创建对象时，其默认公共权限是继承权限，而继承权限的返回值是1，因此在客户端创建对象时不要用继承权限，创建后一定要手动指定权限！！
    private Integer aclFlag = 2; //1是私有读写，2是公有读私有写，3是公有读写
		
}
