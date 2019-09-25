package com.baoxue.spartacus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.baoxue.spartacus.pojo.UserEntity;


/**
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年2月28日 下午5:43:56
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

//	@Query(value="select id,username,password,roles from tb_users where username=?1", nativeQuery=true)
	UserEntity findByUsername(String username);
}
