package com.baoxue.spartacus.service;

import com.baoxue.spartacus.pojo.UserEntity;
import com.baoxue.spartacus.repository.UserEntityRepository;
import com.baoxue.spartacus.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	UserEntityRepository userEntityRepository;
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	

	/**
     * 根据用户名返回用户完整信息
     *  
     * @author lvchao 2018年2月28日 下午6:06:47
     * @param username 用户名唯一
     * @return
     */
    public UserEntity findByUsername(String username) {
    	UserEntity userEntity = userEntityRepository.findByUsername(username);
        return userEntity;
    }
    

    /**
     * 根据用户名查询用户是否存在
     *  
     * @author lvchao 2018年2月28日 下午6:07:14
     * @param username 用户名唯一
     * @return
     */
    public boolean isExist(String username){
        UserEntity userEntity = userEntityRepository.findByUsername(username);
        if(!CommonUtils.isNull(userEntity)) {
        	return true;
        } else {
        	return false;
        }
    }


    /**
	 * 添加用户
	 *  
	 * @author lvchao 2019年5月20日
	 * @param user
	 * @return
	 */
	public UserEntity add(UserEntity user) {
		UserEntity userEntity = userEntityRepository.saveAndFlush(user);
		return userEntity;
	}

}
