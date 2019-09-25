package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.pojo.AccessForbid;
import com.baoxue.spartacus.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.req.BaseReq;
import com.baoxue.spartacus.controller.resp.BaseResp;

/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年10月25日 下午6:19:48
 */
@RestController
@RequestMapping("/access")
public class AccessController {
	
	@Autowired
    AccessService accessService;
	
	
	@RequestMapping("/getStatistics")
	public BaseResp getStatistics() throws BlogException {
	    return accessService.getStatistics();
	}
	
	@RequestMapping("/getScanRecords")
	public BaseResp getScanRecords(@ModelAttribute BaseReq baseReq) throws BlogException {
	    return accessService.getScanRecords(baseReq.getFlag(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}
	
	@RequestMapping("/getHighFrequencyAccessess")
	public BaseResp getHighFrequencyAccessess(@ModelAttribute BaseReq baseReq) throws BlogException {
	    return accessService.getHighFrequencyAccesses(baseReq.getFlag(), baseReq.getCurrentPage(), baseReq.getPageSize());
	}
	
	@RequestMapping("/forbid")
	public BaseResp forbid(@ModelAttribute AccessForbid forbid) throws BlogException {
	    return accessService.forbid(forbid);
	}
	
	@RequestMapping("/unForbid")
	public BaseResp unForbid(String ip) throws BlogException {
	    return accessService.unForbid(ip);
	}
	
	@RequestMapping(value = "/getForbids", method = RequestMethod.POST)
	public BaseResp getForbids(String flag, Integer currentPage, Integer pageSize) throws BlogException {
		return accessService.getForbids(flag, currentPage, pageSize);
	}
	
	@RequestMapping(value = "/batchUnForbid", method = RequestMethod.POST)
    public BaseResp batchUnForbid(String jsonParams) throws BlogException {
        return accessService.batchUnForbid(jsonParams);
    }
}
