package com.baoxue.spartacus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.baoxue.spartacus.pojo.AccessForbid;

@Repository
public interface AccessForbidRepository  extends JpaRepository<AccessForbid, Long>, JpaSpecificationExecutor<AccessForbid> {

	public Integer countByIp(String ip);

	public void deleteByIp(String ip);
	
}
