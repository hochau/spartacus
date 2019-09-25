package com.baoxue.spartacus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.baoxue.spartacus.pojo.Mission;


/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月18日上午9:01:04
 */
public interface MissionRepository extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {
	
	@Query(value="SELECT * FROM tb_mission ORDER BY exe_time DESC LIMIT 20", nativeQuery = true)
	public List<Mission> getRecentMissions();
	
}
