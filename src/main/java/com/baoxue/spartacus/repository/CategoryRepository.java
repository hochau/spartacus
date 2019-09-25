package com.baoxue.spartacus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.baoxue.spartacus.pojo.Category;


/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月18日上午9:01:04
 */
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

//	@Query("from User u where u.name=:n")
//	List<Article> find(@Param("n") String name);
	
//	@Modifying
//	@Query(value="insert into sys_user_has_app(app_id,sys_user_id) values(?,?)",nativeQuery=true)
//	void addSysUserApp(@Param("app_id")Integer app_id, @Param("sys_user_id")Integer sys_user_id);
}
