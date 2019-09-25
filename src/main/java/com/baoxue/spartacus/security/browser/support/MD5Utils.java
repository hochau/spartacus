package com.baoxue.spartacus.security.browser.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * MD5加密工具，不可逆，主要用于注册时，把明文密码加密后存到数据库
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2019年5月20日 下午7:11:31
 */
public class MD5Utils {

    public static String encode(String text){
        try {
            MessageDigest digest =MessageDigest.getInstance("md5");
            byte [] result=digest.digest(text.getBytes());
            StringBuilder sb=new StringBuilder();
            for(byte b:result){
                int number=b&0xff;
                String hex=Integer.toHexString(number);
                if(hex.length()==1){
                    sb.append("0"+hex);
                }else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            
            return "";
        }
    }
    
}