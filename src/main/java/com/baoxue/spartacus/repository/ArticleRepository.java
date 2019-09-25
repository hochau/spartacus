package com.baoxue.spartacus.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baoxue.spartacus.pojo.Article;


/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月18日上午9:01:04
 */
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
	
	@Query(value="select new Article(id,title,author,labels,publishTime,commentNumber,scanNumber,cname,fromWhere,status,isTop,brief,year,monthDay,pictures) from Article a where a.status=?1")
	public List<Article> findByStatus(Integer status);
	
	@Query(value="select new Article(id,title,author,labels,publishTime,commentNumber,scanNumber,cname,fromWhere,status,isTop,brief,year,monthDay,pictures) from Article a where a.status=?1")
	public Page<Article> findByStatus(Integer status, Pageable pageRequest);
	
//	@Modifying
//	@Query(value = "update tb_article set title=?1,author=?2,labels=?3,publish_time=?4,cname=?5,from_where=?6,brief=?7,month_day=?8,year=?9,pictures=?10,content=?11 where id=?12", nativeQuery = true)
//	public Integer update(String title, String author, String labels, Date publishTime, String cname, String fromWhere, String brief, String monthDay, String year, String pictures, String content, String id);

	@Modifying
	@Query(value = "update tb_article set comment_number=comment_number+(?1) where id=?2", nativeQuery = true)
	public Integer modifyCommentNumber(Integer increaseNumber, Long id);
}
