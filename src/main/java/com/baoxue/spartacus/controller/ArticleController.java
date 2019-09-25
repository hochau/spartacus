package com.baoxue.spartacus.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.service.ArticleService;
import com.baoxue.spartacus.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.req.ArticleReq;
import com.baoxue.spartacus.controller.req.BaseReq;
import com.baoxue.spartacus.controller.resp.BaseResp;

/**
 *   
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年10月25日 下午6:19:54
 */
@RestController
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
    CategoryService categoryService;
		
	@Autowired
	private ArticleService articleService;


	@RequestMapping(value = "/syncData")
	public BaseResp syncData() {
		return articleService.syncData();
	}

	@RequestMapping("/getArticle/{id}")
	public BaseResp getArticle(@PathVariable Long id, HttpServletRequest request) throws BlogException {
		return articleService.getArticle(id);
	}

	
	@RequestMapping("/findByStatus")
	public BaseResp findByStatus(@ModelAttribute BaseReq baseReq, HttpServletRequest request) throws BlogException {
		return articleService.findByStatus(baseReq.getStatus(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}
	
	
	@RequestMapping("/search")
	public BaseResp search(@ModelAttribute BaseReq baseReq, HttpServletRequest request) throws BlogException {
		return articleService.search(baseReq.getSearchContent(), baseReq.getStatus(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}
	
	
	@RequestMapping(value = "/getCategory", method = RequestMethod.GET)
	public BaseResp getCategory(HttpSession session) throws BlogException {
		return categoryService.findAll();
    }
	
	
	@RequestMapping(value = "/release", method = RequestMethod.POST)
	public BaseResp release( @ModelAttribute ArticleReq articleReq) throws BlogException {
		return articleService.release(articleReq);
	}


    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    public BaseResp draft(@ModelAttribute ArticleReq articleReq) throws BlogException {
        return articleService.draft(articleReq);
    }
    
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResp update(@ModelAttribute ArticleReq articleReq) throws BlogException {
        return articleService.update(articleReq);
    }
    
}
