package com.baoxue.spartacus.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2017年12月20日下午12:04:49
 *
 * @description: 通用小工具
 */
public class CommonUtils {
	
	private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	
	
    /**
     * 判断参数是否为空， 空返回true ，不为空返回false
     * @remark
     * @author yezm
     * @createTIme 2016-6-15 上午11:42:42
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj){
        if(obj == null) {
            return true;
        }
        if("".equals(obj)) {
            return true;
        }

        return false;
    }

    /**
     * 获取Html文本中的图片链接
     *  
     * @author lvchao 2018年2月24日 下午4:08:27
     * @param htmlContent
     * @return
     */
    public static String getPictures(String htmlContent) {
    	String pictures = "";
    	if(!CommonUtils.isNull(htmlContent)) {
	    	Document doc = Jsoup.parse(htmlContent);
	    	Elements es = doc.getElementsByTag("img");
	    	for(Element e : es) {
	    		pictures = pictures + ";" + e.attr("src");
	    	}
    	}
    	return pictures;
    }
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2018年1月16日 下午2:17:48
	 * @description 获取文章前150字的摘要
	 *
	 * @param htmlContent
	 * @return
	 */
	public static String getBrief(String htmlContent) {
		if(!CommonUtils.isNull(htmlContent)) {
			Document doc = Jsoup.parse(htmlContent);
			if(doc.text().length() >=150) {
				return doc.text().substring(0, 150);
			} else {
				return doc.text();
			}
		}
		return "";
	}
	
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月20日 下午12:01:19
	 * @description 返回全部小写的UUID字符串
	 *
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月20日 下午12:01:47
	 * @description 根据给定的时间格式，返回当前时间
	 *
	 * @param format
	 * @return
	 */
	public static String getDateString(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
	}
	
	/**
	 * 根据给定的时间格式，返回给定时间字符串
	 *  
	 * @author lvchao 2018年2月7日 下午4:41:20
	 * @param format
	 * @return
	 */
	public static String getDateString(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
	}
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月20日 下午12:02:12
	 * @description 根据给定的正则表达式，从待匹配串中返回第一个匹配的字串
	 *
	 * @param regex
	 * @param source
	 * @return
	 */
	public static String getMatcher(String regex, String source) {
		String result = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			result = matcher.group();
		}
		return result;
	}
	
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月20日 上午11:59:33
	 * @description 根据时间字符串（格式必须是 yyyy-mm-dd）返回对应的秒值
	 *
	 * @param dateString
	 * @return
	 */
	public static int getDateSeconds(String dateString) {
		String str = getMatcher("\\d{4}-\\d{2}-\\d{2}", dateString);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			logger.error("时间解析出错，请检查格式是否正确！", e);
		}
        NumberFormat nf = NumberFormat.getInstance();
    	nf.setGroupingUsed(false);
        return Integer.parseInt(nf.format(date.getTime()/1000.0));
	}
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月20日 上午11:59:33
	 * @description 根据时间返回对应的秒值
	 *
	 * @param date
	 * @return
	 */
	public static int getDateSeconds(Date date) {
		if(date != null) {
			return (int)(date.getTime()/1000.0);
		} else {
			return (int)(new Date().getTime()/1000.0);
		}
	}
	
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月21日 上午10:09:01
	 * @description 判断当前时间是否在指定的时间段内
	 *
	 * @param beginTimeStr HH:mm:ss
	 * @param endTimeStr HH:mm:ss
	 * @return true/false
	 */
	public static boolean isInPeriod(String beginTimeStr, String endTimeStr){
	    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	    Date nowTime =null;
	    Date beginTime = null;
	    Date endTime = null;
	    try {
	        nowTime = df.parse(df.format(new Date()));
	        beginTime = df.parse(beginTimeStr);
	        endTime = df.parse(endTimeStr);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (now.after(begin) && now.before(end)) {
            return true;
        } else {
            return false;
        }
    }
	
	/**
	 * 
	 * @author lvchao
	 * @createtime 2017年12月21日 上午10:09:01
	 * @description 判断指定的时间是否在指定的时间段内
	 *
	 * @param beginTimeStr HH:mm:ss
	 * @param endTimeStr HH:mm:ss
	 * @return true/false
	 */
	public static boolean isInPeriod(Date date, String beginTimeStr, String endTimeStr){
	    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	    Date nowTime =null;
	    Date beginTime = null;
	    Date endTime = null;
	    try {
	        nowTime = df.parse(df.format(date));
	        beginTime = df.parse(beginTimeStr);
	        endTime = df.parse(endTimeStr);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
        Calendar now = Calendar.getInstance();
        now.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (now.after(begin) && now.before(end)) {
            return true;
        } else {
            return false;
        }
    }
}
