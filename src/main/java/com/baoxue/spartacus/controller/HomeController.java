package com.baoxue.spartacus.controller;

import com.baoxue.spartacus.repository.AccessRepository;
import com.baoxue.spartacus.task.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

	@Autowired
    AsyncTask task;
	
	@Autowired
    AccessRepository accessRepository;
	
	
	/*@GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws BlogException {
		Cookie[] cookies = request.getCookies();
        if (null != cookies) {
        	boolean flag = false;
            for (Cookie cookie : cookies) {
            	if(cookie.getName().equals("aaa")) {
            		flag = true;
            		System.out.println(cookie.getName()+": "+ cookie.getValue());
            	}
            }
            if(flag == false) {
            	Cookie cookie1 = new Cookie("aaa", "AAA");
        		cookie1.setMaxAge(30); // cookie1在硬盘上保存2分钟
        		response.addCookie(cookie1);
            }
        }
		JSONObject jsonObj = new JSONObject();
		request.getSession().setAttribute("todayScanDetails", jsonObj.toJSON(accessRecordRepository.getTodayScanDetails()));
		request.getSession().setAttribute("monthScanDetails", jsonObj.toJSON(accessRecordRepository.getMonthScanDetails()));
		request.getSession().setAttribute("yearScanDetails", jsonObj.toJSON(accessRecordRepository.getYearScanDetails()));
        return "login";
    }*/
	
	/*@GetMapping("/index")
	public String index(HttpServletRequest request) throws BlogException {
		return "index-1";
	}*/

}
