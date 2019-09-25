package com.baoxue.spartacus.repository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.pojo.NetIOObject;
import com.baoxue.spartacus.utils.CommonUtils;

/**
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年10月19日 上午10:38:41
 */
@Repository
public class NetIORepository {

	@PersistenceContext
	protected EntityManager entityManager;
	
	@Autowired
	BlogProperties blogProperties;
	
	
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getTodayNetIoDetails(String ip) {
		String sql = "SELECT insert_date As insertDate, io_in AS ioIn, io_out AS ioOut FROM tb_net_io WHERE ip='" + ip + "' AND insert_date BETWEEN DATE_ADD(CURDATE(), INTERVAL 0 DAY) AND DATE_ADD(CURDATE(), INTERVAL 1 DAY)";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(NetIOObject.class));
		List<NetIOObject> list = (List<NetIOObject>)nativeQuery.getResultList();
		return processDay(list);
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getMonthNetIoDetails(String ip) {
		String sql = "SELECT DATE_FORMAT(insert_date,'%d') As insertDate, SUM(io_in) AS ioIn, SUM(io_out) AS ioOut FROM tb_net_io WHERE ip='" + ip + "' AND DATE_FORMAT(insert_date,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY DATE_FORMAT(insert_date,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(NetIOObject.class));
		List<NetIOObject> list = (List<NetIOObject>)nativeQuery.getResultList();
		return processMonth(list);
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getYearNetIoDetails(String ip) {
		String sql = "SELECT DATE_FORMAT(insert_date,'%m') As insertDate,SUM(io_in) AS ioIn,SUM(io_out) AS ioOut FROM tb_net_io WHERE ip='" + ip + "' AND DATE_FORMAT(insert_date,'%Y')=DATE_FORMAT(NOW(),'%Y') GROUP BY DATE_FORMAT(insert_date,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(NetIOObject.class));
		List<NetIOObject> list = (List<NetIOObject>)nativeQuery.getResultList();
		return processYear(list);
	}
	
	
	
	public Map<Object, Object> processDay(List<NetIOObject> list) {
		Map<Object, Object> map = new LinkedHashMap<>();
		Map<Object, Object> inMap = new LinkedHashMap<>();
		Map<Object, Object> outMap = new LinkedHashMap<>();
		for(int i=0; i <= 24; i++) {
			if(i < 10) {
				inMap.put("0" + i + ":00", 0);
				outMap.put("0" + i + ":00", 0);
			} else {
				inMap.put(i + ":00", 0);
				outMap.put(i + ":00", 0);
			}
		}
		
		//标记是否有第二天0点0分0秒的数据
		boolean hasTomorrowZeroClockNetIoData = false;
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(int i = 0; i < list.size(); i++) {
			String time = CommonUtils.getDateString("HH:mm", (Date)list.get(i).getInsertDate());
			inMap.put(time, list.get(i).getIoIn());
			outMap.put(time, list.get(i).getIoOut());
			
			if(time.equals("00:00")) {
				if(fmt.format(cal.getTime()).equals(fmt.format((Date)list.get(i).getInsertDate()))) {
					hasTomorrowZeroClockNetIoData = true;
				}
			}
		}
		
		if(hasTomorrowZeroClockNetIoData) {
			inMap.put("24:00", inMap.get("00:00"));
			outMap.put("24:00", outMap.get("00:00"));
		}
		inMap.put("00:00", 0f);
		outMap.put("00:00", 0f);
		map.put("ioIn", inMap);
		map.put("ioOut", outMap);		
		return map;
	}
	
	public Map<Object, Object> processMonth(List<NetIOObject> list) {
		Map<Object, Object> map = new LinkedHashMap<>();
		Map<Object, Object> inMap = new LinkedHashMap<>();
		Map<Object, Object> outMap = new LinkedHashMap<>();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);//把日期设置为当月第一天
	    cal.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int days = cal.get(Calendar.DATE);
		for(int i=1; i<=days; i++) {
			inMap.put(i, 0);
			outMap.put(i, 0);
		}
		for(NetIOObject vo : list) {
			inMap.put(Integer.parseInt(vo.getInsertDate().toString()), vo.getIoIn());
			outMap.put(Integer.parseInt(vo.getInsertDate().toString()), vo.getIoOut());
		}
		
		map.put("ioIn", inMap);
		map.put("ioOut", outMap);
		return map;
	}
	
	public Map<Object, Object> processYear(List<NetIOObject> list) {
		Map<Object, Object> map = new LinkedHashMap<>();
		Map<Object, Object> inMap = new LinkedHashMap<>();
		Map<Object, Object> outMap = new LinkedHashMap<>();
		
		for(int i=1; i<=12; i++) {
			inMap.put(i, 0);
			outMap.put(i, 0);
		}
		for(NetIOObject vo : list) {
			inMap.put(Integer.parseInt(vo.getInsertDate().toString()), vo.getIoIn());
			outMap.put(Integer.parseInt(vo.getInsertDate().toString()), vo.getIoOut());
		}
		
		map.put("ioIn", inMap);
		map.put("ioOut", outMap);
		return map;
	}
	
}