package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.exception.BlogException;
import com.baoxue.spartacus.service.MissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baoxue.spartacus.controller.resp.BaseResp;

/**
 * 
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年10月25日 下午5:20:58
 */
@RestController
@RequestMapping("/mission")
public class MissionController {
	
	@Autowired
    MissionService missionService;
	
	@RequestMapping("/getRecentMissions")
	public BaseResp getRecentMissions() throws BlogException {
	    return missionService.getRecentMissions();
	}
}
