package com.baoxue.spartacus.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baoxue.spartacus.service.CosService;


/**
 * 定时更新刷新redis中COS统计数据，以防用户通过腾讯云控制台增删改查COS对象
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2017年12月20日下午5:54:46
 */
@Component
public class ScheduledTask {

	@Autowired
	AsyncTask task;

	@Autowired
	CosService cosService;

//	@Scheduled(cron = "0 0/1 * * * *")
//	public void sseSend() {
//		task.sseSend("1111");
//	}

	/*@Scheduled(cron = "0 0/1 * * * *")
	public void refreshDirectoryTreesInRedis() {
		cosService.refreshDirectoryTreesInRedis();
	}
	}*/
	
}
