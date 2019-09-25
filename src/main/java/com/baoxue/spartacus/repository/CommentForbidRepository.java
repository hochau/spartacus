package com.baoxue.spartacus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baoxue.spartacus.pojo.CommentForbid;

@Repository
public interface CommentForbidRepository  extends JpaRepository<CommentForbid, Long>, JpaSpecificationExecutor<CommentForbid> {

	@Query(value="SELECT ip FROM tb_comment_forbid WHERE forbid_type=1", nativeQuery = true)
	public List<String> getForbiddenIpList();
	
	@Query(value="SELECT provider_user_id FROM tb_comment_forbid WHERE forbid_type=0", nativeQuery = true)
	public List<String> getForbiddenProviderUserIdList();
	
	public Integer deleteByIpAndForbidType(String ip, Integer forbidType);
	
	public Integer deleteByProviderUserIdAndForbidType(String providerUserId, Integer forbidType);
	
	public Page<CommentForbid> findByForbidType(Integer forbidType, Pageable pageable);
	
}