package com.baoxue.spartacus.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * 用户实体类
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年2月28日 下午6:00:59
 */
@Entity
@Table(name ="tb_users_entity")
@DynamicUpdate(true)
@DynamicInsert(true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@JsonSerialize(using= ToStringSerializer.class)
	private Long id;

	@Column(nullable = false)
    private String username;

	@Column(nullable = false)
    private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private String headImg;

	@Column(nullable = false)
    private String roles;

}
