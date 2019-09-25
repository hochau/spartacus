package com.baoxue.spartacus.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年4月16日下午8:08:04
 *
 * @description: bean、map之间相互转换
 */
public class BeanUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	/**
	 * 获取对象的null值属性名
	 *  
	 * @author lvchao 2018年11月5日
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames (Object source) {
		try {
			final BeanWrapper src = new BeanWrapperImpl(source);
	        PropertyDescriptor[] pds = src.getPropertyDescriptors();

	        Set<String> emptyNames = new HashSet<String>();
	        for(PropertyDescriptor pd : pds) {
	            Object srcValue = src.getPropertyValue(pd.getName());
	            if (srcValue == null) emptyNames.add(pd.getName());
	        }
	        String[] result = new String[emptyNames.size()];
	        return emptyNames.toArray(result);
		} catch (Exception e) {
			logger.error("获取null值字段名称失败！", e);
		}
		
		return null;
    }

	/**
	 * Javabean之间的属性拷贝，但是忽略null值属性的拷贝
	 *  
	 * @author lvchao 2018年11月5日
	 * @param dataObj
	 * @param targetObj
	 */
    public static void copyProperties(Object dataObj, Object targetObj) {
        org.springframework.beans.BeanUtils.copyProperties(dataObj, targetObj, getNullPropertyNames(dataObj));
    }
	
	
    /**
     * Map --> Bean: 利用Introspector, PropertyDescriptor
     *  
     * @author lvchao
     * @createtime 2018年4月17日 上午9:05:03
     *
     * @param map
     * @param obj
     */
    public static void map2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 从ES中返回
                    if(value != null && value.toString().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    	value = sdf.parse(value.toString());
                    }
                    Method setter = property.getWriteMethod(); // 得到property对应的setter方法
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
        	logger.error("Convert Map to Bean Error!", e);
        }
    }
    

    /**
     * Bean --> Map: 利用Introspector, PropertyDescriptor
     *
     * @author lvchao
     * @createtime 2018年4月17日 上午9:31:17
     *
     * @param obj
     * @param map
     */
    public static void bean2Map(Object obj, Map<String, Object> map) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) { // 过滤class属性
                    Method getter = property.getReadMethod(); // 得到property对应的getter方法
                    Object value = getter.invoke(obj);
                    //存储到ES
                    if(value instanceof Date) {
                    	value = CommonUtils.getDateString("yyyy-MM-dd HH:mm:ss", (Date)value);
                    }
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
        	logger.error("Convert Bean To Map Error!", e);
        }
    }
    
}