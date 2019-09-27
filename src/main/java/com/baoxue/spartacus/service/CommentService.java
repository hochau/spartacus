package com.baoxue.spartacus.service;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeMap;

import com.baoxue.spartacus.pojo.Comment;
import com.baoxue.spartacus.pojo.CommentForbid;
import com.baoxue.spartacus.pojo.UserEntity;
import com.baoxue.spartacus.repository.ArticleRepository;
import com.baoxue.spartacus.repository.CommentForbidRepository;
import com.baoxue.spartacus.repository.CommentRepository;
import com.baoxue.spartacus.utils.CommonUtils;
import com.baoxue.spartacus.utils.HttpUtils;
import com.baoxue.spartacus.utils.IPUtils;
import com.baoxue.spartacus.utils.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.controller.resp.BaseResp;
import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.globals.Globals;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年3月14日 下午4:07:15
 */
@Service
public class CommentService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	BlogProperties blogProperties;
	
	@Autowired
    CommentRepository commentRepository;
	
	@Autowired
    ArticleRepository articleRepository;
	
	@Autowired
    CommentForbidRepository commentForbidRepository;

	@Autowired
	UserService userService;

	/**
	 * 插入一条评论
	 *  
	 * @author lvchao 2018年3月14日 下午4:06:18
	 * @param comment
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp insert(Comment comment, HttpServletRequest request) throws BlogException {
		try {
			logger.info("insert->开始导入评论...");
			comment.setId(Snowflake.generateId());
			comment.setStatus(0);

			///////
			String ip = IPUtils.getIpAddr(request);
			comment.setIp(ip);

			String url = blogProperties.getBaiduUrl() +"&ak="+blogProperties.getBaiduAk()+"&ip="+ip;
			String result = HttpUtils.doGetMap(url, null);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.getInteger("status") == 0) {
				String province = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("province");
				String city = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("city");
				comment.setIpCity(province +","+ city);
			}
			///////

			if(CommonUtils.isNull(comment.getRefId())) { //一级评论
				commentRepository.saveAndFlush(comment);
			} else { //二级评论
				comment.setLevel(2);
				commentRepository.saveAndFlush(comment);
			}
			logger.info("insert->导入成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, comment.getId()); //返回评论的id
		} catch (Exception e) {
			logger.error("insert->导入失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 插入一条评论
	 *
	 * @author lvchao 2018年3月14日 下午4:06:18
	 * @param comment
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp adminInsert(Comment comment, HttpServletRequest request) throws BlogException {
		try {
			logger.info("adminInsert->开始导入评论...");
			comment.setId(Snowflake.generateId());
			comment.setStatus(1);

			comment.setNickname(blogProperties.getAdminNickname());
			comment.setHeadImg(blogProperties.getAdminHeadImg());

			///////
			String ip = IPUtils.getIpAddr(request);
			comment.setIp(ip);

			String url = blogProperties.getBaiduUrl() +"&ak="+blogProperties.getBaiduAk()+"&ip="+ip;
			String result = HttpUtils.doGetMap(url, null);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.getInteger("status") == 0) {
				String province = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("province");
				String city = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("city");
				comment.setIpCity(province +","+ city);
			}
			///////

			if(CommonUtils.isNull(comment.getRefId())) { //一级评论
				commentRepository.saveAndFlush(comment);
			} else { //二级评论
				comment.setLevel(2);
				commentRepository.saveAndFlush(comment);
			}

			articleRepository.modifyCommentNumber(1, comment.getArticleId());
			logger.info("adminInsert->导入成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, comment.getId()); //返回评论的id
		} catch (Exception e) {
			logger.error("adminInsert->导入失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
	 * 管理员回复一条评论，管理员回复这条评论、被回复的评论，都直接通过
	 *  
	 * @author lvchao 2018年3月14日 下午4:06:18
	 * @param comment
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp adminReplyComment(Comment comment, HttpServletRequest request) throws BlogException {
		try {
			logger.info("adminReplyComment->管理员回复评论...");
			Integer count = 0;
			comment.setId(Snowflake.generateId());
			comment.setStatus(1);
			comment.setLevel(2);

			comment.setNickname(blogProperties.getAdminNickname());
			comment.setHeadImg(blogProperties.getAdminHeadImg());

			///////
			String ip = IPUtils.getIpAddr(request);
			comment.setIp(ip);

			String url = blogProperties.getBaiduUrl() +"&ak="+blogProperties.getBaiduAk()+"&ip="+ip;
			String result = HttpUtils.doGetMap(url, null);
			JSONObject jsonObject = JSONObject.parseObject(result);
			if(jsonObject.getInteger("status") == 0) {
				String province = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("province");
				String city = jsonObject.getJSONObject("content").getJSONObject("address_detail").getString("city");
				comment.setIpCity(province +","+ city);
			}
			///////

			commentRepository.saveAndFlush(comment);
			count++;

			Comment linkedComment = commentRepository.findOne(comment.getFrontId());
			if(linkedComment.getStatus() != 1) {
				linkedComment.setStatus(1);
				commentRepository.saveAndFlush(linkedComment);
				count++;
			}
			
			articleRepository.modifyCommentNumber(count, comment.getArticleId());
			logger.info("adminReplyComment->回复成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, comment.getId()); //返回评论的id
		} catch (Exception e) {
			logger.error("adminReplyComment->回复失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 获取最近的20条评论
	 *  
	 * @author lvchao 2018年10月25日
	 * @return
	 * @throws BlogException
	 */
	public BaseResp getRecentComments() throws BlogException {
		try {
			logger.info("getRecentComments->开始获取最近20条评论...");
			List<Comment> list = commentRepository.getRecentComments();
			logger.info("getRecentComments->获取成功！");
	    	return new BaseResp(Globals.CODE_0, Globals.MSG_0, list);
	    } catch (Exception e) {
	    	logger.error("getRecentComments-->获取失败！", e);
	    }
	    return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
	 * 分页返回评论数据
	 *  
	 * @author lvchao 2018年3月14日 下午4:27:43
	 * @param status
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws BlogException
	 */
	public BaseResp findByStatus(Integer status, Integer currentPage, Integer pageSize) throws BlogException {
		try {
			logger.info("findByStatus->开始获取评论...");
			Order publishTimeOrder = new Order(Direction.DESC, "publishTime");
	        Sort sort = new Sort(publishTimeOrder);
	        PageRequest pageRequest  = new PageRequest(currentPage-1, pageSize, sort);
	        Page<Comment> page = commentRepository.findByStatus(status, pageRequest);
	        
	        HashMap<Object, Object> pageMap = new HashMap<Object, Object>();
	        pageMap.put("forbiddenIps", commentForbidRepository.getForbiddenIpList());
	        pageMap.put("forbiddenIds", commentForbidRepository.getForbiddenProviderUserIdList());
	        pageMap.put("comments", page.getContent());
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", page.getTotalPages());
	        pageMap.put("total", page.getTotalElements());
	        logger.info("findByStatus->获取成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageMap);
		} catch (Exception e) {
			logger.error("findByStatus->获取失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	/**
	 * 返回所有评论数据
	 *  
	 * @author lvchao 2018年3月14日 下午4:28:22
	 * @param status
	 * @return
	 * @throws BlogException
	 */
	public BaseResp findByArticleIdAndStatus(Long articleId, Integer status, Integer currentPage, Integer pageSize) throws BlogException {
		try {
			logger.info("findByArticleIdAndStatus->开始获取评论...");
			Order publishTimeOrder = new Order(Direction.ASC, "publishTime");
	        Sort sort = new Sort(publishTimeOrder);
			PageRequest pageRequest  = new PageRequest(currentPage-1, pageSize, sort);
			Page<Comment> page = commentRepository.findByArticleIdAndStatusAndLevel(articleId, status, 1, pageRequest);
			TreeMap<Long, Comment> commentMap = new TreeMap<Long, Comment>();
			for(Comment comment1 : page.getContent()) {
				List<Comment> subComments = commentRepository.findByRefIdAndStatusAndLevel(comment1.getId(), status, 2);
				TreeMap<Long, Comment> subCommentMap = new TreeMap<Long, Comment>();
				for(Comment comment2 : subComments) {
					subCommentMap.put(comment2.getId(), comment2);
				}
				comment1.setSubComments(subCommentMap);
				commentMap.put(comment1.getId(), comment1);
			}

			HashMap<Object, Object> pageMap = new HashMap<Object, Object>();
	        pageMap.put("comments", commentMap);
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", page.getTotalPages());
	        pageMap.put("total", page.getTotalElements());
			logger.info("findByArticleIdAndStatus->获取成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageMap);
		} catch (Exception e) {
			logger.error("findByArticleIdAndStatus->获取失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	
	/**
	 * 更新评论状态，发生异常时整个事务回滚
	 *  
	 * @author lvchao 2018年11月5日
	 * @return
	 * @throws BlogException
	 */
    @Transactional
    public BaseResp setStatus(Long articleId, Long commentId, Integer oldStatus, Integer newStatus) throws BlogException {
		try {
			logger.info("setStatus->开始更新评论状态... ");
			int count = commentRepository.setStatus(commentId, newStatus);
			if(oldStatus != 1 && newStatus == 1) {
				articleRepository.modifyCommentNumber(count, articleId);
			} else if(oldStatus == 1 && newStatus != 1){
				articleRepository.modifyCommentNumber(count * -1, articleId);
			}
			logger.info("setStatus->更新成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("setStatus->更新失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
    }
	
    /**
	 * 批量更新评论状态，发生异常时整个事务回滚
	 *  
	 * @author lvchao 2018年11月6日
	 * @return
	 * @throws BlogException
	 */
    @Transactional
    public BaseResp batchSetStatus(String jsonParams) throws BlogException {
		try {
			logger.info("batchSetStatus->开始批量更新评论状态... ");
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				Long articleId = jsonArray.getJSONObject(i).getLong("articleId");
				Long commentId = jsonArray.getJSONObject(i).getLong("commentId");
				Integer oldStatus = jsonArray.getJSONObject(i).getInteger("oldStatus");
				Integer newStatus = jsonArray.getJSONObject(i).getInteger("newStatus");
				int count = commentRepository.setStatus(commentId, newStatus);
				if(oldStatus != 1 && newStatus == 1) {
					articleRepository.modifyCommentNumber(count, articleId);
				} else if(oldStatus == 1 && newStatus != 1){
					articleRepository.modifyCommentNumber(count * -1, articleId);
				}
			}			
			logger.info("batchSetStatus->批量更新成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchSetStatus->批量更新失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
    }
	
	
    /**
	 * 删除评论及其追评，发生异常时整个事务回滚
	 *  
	 * @author lvchao 2018年11月5日
	 * @param commentId
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp delete(Long articleId, Long commentId, Integer status, Integer level) throws BlogException {
		try {
			logger.info("delete->开始删除评论... ");
			int count = 0;
			if(level == 1) {
				count += commentRepository.countByRefIdAndStatus(commentId, 1);
				commentRepository.deleteByRefId(commentId);
			} else if(level == 2) {
				PriorityQueue queue = new PriorityQueue<String>();
				List<Long> ids = commentRepository.getLinkedCommentIdList(commentId);
				queue.addAll(ids);
				while(!queue.isEmpty()) {
					Long id = (Long) queue.poll();
					ids = commentRepository.getLinkedCommentIdList(id);
					queue.addAll(ids);
					if(commentRepository.getOne(id).getStatus() == 1) {
						count++;
					}
					commentRepository.delete(id);
				}
			}
			if(status == 1) {
				count++;
			}
			commentRepository.delete(commentId);
			articleRepository.modifyCommentNumber(count * -1, articleId);
			logger.info("delete->删除成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("delete->删除失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
		
	}

	/**
	 * 批量删除评论及其追评，发生异常时整个事务回滚
	 *  
	 * @author lvchao 2018年11月5日
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp batchDelete(String jsonParams) throws BlogException {
		try {
			logger.info("batchDelete->开始批量删除评论...");
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				Long commentId = jsonArray.getJSONObject(i).getLong("commentId");
				Integer status = jsonArray.getJSONObject(i).getInteger("status");
				Integer level = jsonArray.getJSONObject(i).getInteger("level");
				Long articleId = jsonArray.getJSONObject(i).getLong("articleId");
				
				int count = 0;
				if(level == 1) {
					count += commentRepository.countByRefIdAndStatus(commentId, 1);
					commentRepository.deleteByRefId(commentId);
				} else if(level == 2) {
					PriorityQueue queue = new PriorityQueue<String>();
					List<Long> ids = commentRepository.getLinkedCommentIdList(commentId);
					queue.addAll(ids);
					while(!queue.isEmpty()) {
						Long id = (Long) queue.poll();
						ids = commentRepository.getLinkedCommentIdList(id);
						queue.addAll(ids);
						if(commentRepository.getOne(id).getStatus() == 1) {
							count++;
						}
						commentRepository.delete(id);
					}
				}
				if(status == 1) {
					count++;
				}
				commentRepository.delete(commentId);
				//批量删除时，如果待删除1级评论下面有2级评论，但是在后台2级评论先做删除操作，会出现假删问题，会报错
				//因为jpa给修改/删除操作加了默认事务，必须等待batchDelete方法执行结束才提交，所以这里用flush()让提交立刻生效
				commentRepository.flush();
				articleRepository.modifyCommentNumber(count * -1, articleId);
			}
			logger.info("batchDelete->批量删除成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchDelete->批量删除失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 加入评论黑名单
	 *  
	 * @author lvchao 2018年11月16日
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp forbid(CommentForbid forbid) throws BlogException {
		try {
			logger.info("forbid->开始加入评论黑名单...");
			forbid.setId(Snowflake.generateId());
			commentForbidRepository.saveAndFlush(forbid);
			logger.info("forbid->导入成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("forbid->导入失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 分页获取评论黑名单
	 *  
	 * @author lvchao 2018年11月19日
	 * @param currentPage
	 * @param pageSize
	 * @return
	 * @throws BlogException
	 */
	public BaseResp getForbids(Integer forbidType, Integer currentPage, Integer pageSize) throws BlogException {
		try {
			logger.info("getForbids->开始获取评论黑名单...");
			Order operateTimeOrder = new Order(Direction.DESC, "operateTime");
	        Sort sort = new Sort(operateTimeOrder);
	        PageRequest pageRequest  = new PageRequest(currentPage-1, pageSize, sort);
	        Page<CommentForbid> page;
	        if(forbidType != null) {
	        	page = commentForbidRepository.findByForbidType(forbidType, pageRequest);
	        } else {
	        	page = commentForbidRepository.findAll(pageRequest);
	        }
	        HashMap<Object, Object> pageMap = new HashMap<Object, Object>();
	        pageMap.put("forbids", page.getContent());
	        pageMap.put("currentPage", currentPage);
	        pageMap.put("pageSize", pageSize);
	        pageMap.put("totalPages", page.getTotalPages());
	        pageMap.put("total", page.getTotalElements());
	        logger.info("getForbids->获取成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0, pageMap);
		} catch (Exception e) {
			logger.error("getForbids->获取失败！", e);
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}

	/**
	 * 移除评论黑名单
	 *  
	 * @author lvchao 2018年11月16日
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp unForbid(CommentForbid forbid) throws BlogException {
		try {
			logger.info("unForbid->开始解封评论黑名单...");
			if(forbid.getForbidType() == 0) {
				commentForbidRepository.deleteByProviderUserIdAndForbidType(forbid.getProviderUserId(), forbid.getForbidType());
			} else if(forbid.getForbidType() == 1) {
				commentForbidRepository.deleteByIpAndForbidType(forbid.getIp(), forbid.getForbidType());
			}
			logger.info("unForbid->解封成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("unForbid->解封失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
	
	/**
	 * 批量解禁评论黑名单，发生异常时整个事务回滚
	 *  
	 * @author lvchao 2018年11月21日
	 * @param jsonParams
	 * @return
	 * @throws BlogException
	 */
	@Transactional
	public BaseResp batchUnForbid(String jsonParams) throws BlogException {
		try {
			logger.info("batchUnForbid->开始批量解封评论黑名单...");
			JSONArray jsonArray = JSONObject.parseArray(jsonParams);
			for(int i=0; i< jsonArray.size(); i++) {
				Integer forbidType = jsonArray.getJSONObject(i).getInteger("forbidType");
				String ip = jsonArray.getJSONObject(i).getString("ip");
				String providerUserId = jsonArray.getJSONObject(i).getString("providerUserId");
				if(forbidType == 0) {
					commentForbidRepository.deleteByProviderUserIdAndForbidType(providerUserId, forbidType);
				} else if(forbidType == 1) {
					commentForbidRepository.deleteByIpAndForbidType(ip, forbidType);
				}
			}
			logger.info("batchUnForbid->批量解封成功！");
			return new BaseResp(Globals.CODE_0, Globals.MSG_0);
		} catch (Exception e) {
			logger.error("batchUnForbid->批量解封失败！", e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //回滚
		}
		return new BaseResp(Globals.CODE_1, Globals.MSG_1);
	}
	
}
