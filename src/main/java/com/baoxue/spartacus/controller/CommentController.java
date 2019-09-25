package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.pojo.Comment;
import com.baoxue.spartacus.pojo.CommentForbid;
import com.baoxue.spartacus.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.req.BaseReq;
import com.baoxue.spartacus.controller.resp.BaseResp;

import javax.servlet.http.HttpServletRequest;

/**
  * 
  * 
  * @author lvchao
  * @email chao9038@hnu.edu.cn
  * @createtime 2018年10月25日 下午6:20:00
  */
@RestController
@RequestMapping("/comment")
public class CommentController {
		
	@Autowired
	private CommentService commentService;
	

	@RequestMapping("/findByStatus")
	public BaseResp findByStatus(@ModelAttribute BaseReq baseReq) throws BlogException {
		return commentService.findByStatus(baseReq.getStatus(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}
	
	@RequestMapping("/findByArticleIdAndStatus")
	public BaseResp findByArticleIdAndStatus(@ModelAttribute BaseReq baseReq) throws BlogException {
		return commentService.findByArticleIdAndStatus(baseReq.getArticleId(), baseReq.getStatus(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}
	
	@RequestMapping(value = "/submitComment", method = RequestMethod.POST)
	public BaseResp submitComment(@ModelAttribute Comment comment, HttpServletRequest request) throws BlogException {
		return commentService.insert(comment, request);
	}

	@RequestMapping(value = "/adminSubmitComment", method = RequestMethod.POST)
	public BaseResp adminSubmitComment(@ModelAttribute Comment comment, HttpServletRequest request) throws BlogException {
		return commentService.adminInsert(comment, request);
	}
	
	@RequestMapping(value = "/adminReplyComment", method = RequestMethod.POST)
	public BaseResp adminReplyComment(@ModelAttribute Comment comment, HttpServletRequest request) throws BlogException {
		return commentService.adminReplyComment(comment, request);
	}
	
	@RequestMapping("/getRecentComments")
	public BaseResp getRecentComments() throws BlogException {
	    return commentService.getRecentComments();
	}
	
	@RequestMapping(value = "/setStatus", method = RequestMethod.POST)
    public BaseResp setStatus(Long articleId, Long commentId, Integer oldStatus, Integer newStatus) throws BlogException {
        return commentService.setStatus(articleId, commentId, oldStatus, newStatus);
    }
	
	@RequestMapping(value = "/batchSetStatus", method = RequestMethod.POST)
    public BaseResp batchSetStatus(String jsonParams) throws BlogException {
        return commentService.batchSetStatus(jsonParams);
    }
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResp delete(Long articleId, Long commentId, Integer status, Integer level) throws BlogException {
        return commentService.delete(articleId, commentId, status, level);
    }
	
	@RequestMapping(value = "/batchDelete", method = RequestMethod.POST)
    public BaseResp batchDelete(String jsonParams) throws BlogException {
        return commentService.batchDelete(jsonParams);
    }
	
	@RequestMapping(value = "/forbid", method = RequestMethod.POST)
	public BaseResp forbid(@ModelAttribute CommentForbid forbid) throws BlogException {
		return commentService.forbid(forbid);
	}
	
	@RequestMapping(value = "/unForbid", method = RequestMethod.POST)
	public BaseResp unForbid(@ModelAttribute CommentForbid forbid) throws BlogException {
		return commentService.unForbid(forbid);
	}
	
	@RequestMapping(value = "/getForbids", method = RequestMethod.POST)
	public BaseResp getForbids(Integer forbidType, Integer currentPage, Integer pageSize) throws BlogException {
		return commentService.getForbids(forbidType, currentPage, pageSize);
	}
	
	@RequestMapping(value = "/batchUnForbid", method = RequestMethod.POST)
    public BaseResp batchUnForbid(String jsonParams) throws BlogException {
        return commentService.batchUnForbid(jsonParams);
    }
}
