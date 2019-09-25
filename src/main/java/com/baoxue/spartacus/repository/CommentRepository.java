package com.baoxue.spartacus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baoxue.spartacus.pojo.Comment;

/**
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年3月14日 下午4:23:20
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

	Page<Comment> findByStatus(Integer status, Pageable pageable);
	
	List<Comment> findByRefIdAndStatusAndLevel(Long refId, Integer status, Integer level);
	
	Page<Comment> findByArticleIdAndStatusAndLevel(Long articleId, Integer status, Integer level, Pageable pageable);
	
	@Modifying
	@Query(value = "UPDATE tb_comment SET rear_id=?1 WHERE id=?2", nativeQuery = true)
	Integer setRearId(Long rearId, Long id);
	
	@Query(value="SELECT * FROM tb_comment ORDER BY publish_time DESC LIMIT 20", nativeQuery = true)
	List<Comment> getRecentComments();
	
	/*@Query(value="SELECT count(1) FROM tb_comment WHERE status=1 AND ref_id=?1", nativeQuery = true)
	Integer getCountOfPassedCommentByRefId(String refId);*/
	Long countByRefIdAndStatus(Long refId, Integer status);
	
	@Modifying
	@Query(value = "DELETE FROM tb_comment WHERE ref_id=?1", nativeQuery = true)
	Integer deleteByRefId(Long refId);
	
	@Modifying
	@Query(value = "DELETE FROM tb_comment WHERE front_id=?1", nativeQuery = true)
	Integer deleteByFrontId(Long frontId);
	
	@Query(value="SELECT id FROM tb_comment WHERE front_id=?1", nativeQuery = true)
	List<Long> getLinkedCommentIdList(Long id);
	
	@Modifying
	@Query(value = "UPDATE tb_comment SET status=?2 WHERE id=?1", nativeQuery = true)
	Integer setStatus(Long id, Integer status);
}