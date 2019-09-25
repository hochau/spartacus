package com.baoxue.spartacus.repository;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baoxue.spartacus.config.BlogProperties;
import com.baoxue.spartacus.pojo.Access;
import com.baoxue.spartacus.pojo.AccessObject;
import com.baoxue.spartacus.pojo.HighFrequencyAccess;
import com.baoxue.spartacus.utils.CommonUtils;

import lombok.Data;

/**
 * 分时段分析的结果的实体类型
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年9月17日 下午9:07:48
 */
@Data
class Result {
	Integer count = 0; //该时段内次数
	Float avgInterval = 0.0f; //平均间隔时间
	Boolean isRobot = false; //true是机器，false是人
	
	Set<Date> dateSet = new TreeSet<Date>(new Comparator<Date>() {
		
		@Override
		public int compare(Date d1, Date d2) {
			return d1.compareTo(d2);
		}
	});
	
}


/**
 * 
 * @author lvchao
 * @email chao9038@hnu.edu.cn
 * @createtime 2018年5月22日 下午4:22:00
 */
@Repository
public class AccessRepository {

	@PersistenceContext
	protected EntityManager entityManager;
	
	@Autowired
	BlogProperties blogProperties;
	
	@Autowired
	AccessForbidRepository accessForbidRepository;
	
	
	/**
	 * 根据ip获取访问次数
	 *  
	 * @author lvchao 2018年11月26日
	 * @return
	 */
	public Integer getTotalAccessCountByIp(String ip) {
		String sql = "SELECT COUNT(*) FROM tb_access WHERE ip='"+ip+"';";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getTodayAccessCountByIp(String ip) {
		String sql = "SELECT COUNT(*) FROM tb_access WHERE ip='"+ip+"' AND TO_DAYS(access_time)=TO_DAYS(NOW());";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getMonthAccessCountByIp(String ip) {
		String sql = "SELECT COUNT(*) FROM tb_access WHERE ip='"+ip+"' AND DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m');";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getYearAccessCountByIp(String ip) {
		String sql = "SELECT COUNT(*) FROM tb_access WHERE ip='"+ip+"' AND YEAR(access_time)=YEAR(NOW());";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	
	/**
	 * 获取高频访问IP
	 */
	@SuppressWarnings("unchecked")
	public List<HighFrequencyAccess> getTotalHighFrequencyAccessess(Integer currentPage, Integer pageSize) {
		String sql1 = "SELECT ip, ip_city AS ipCity, COUNT(ip) AS count FROM tb_access GROUP BY ip HAVING COUNT(ip) >= " + blogProperties.getAllAccessThreshold();
		Query query1 = entityManager.createNativeQuery(sql1);
		query1.setFirstResult(currentPage * pageSize);
        query1.setMaxResults(pageSize);
		query1.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(HighFrequencyAccess.class));
		List<HighFrequencyAccess> ipList = (List<HighFrequencyAccess>)query1.getResultList();
		for(HighFrequencyAccess access : ipList) {
			String sql2 = "SELECT access_time FROM tb_access WHERE ip='"+access.getIp()+"';";
			Query query2 = entityManager.createNativeQuery(sql2);
			List<Date> dates = (List<Date>)query2.getResultList();
			access.setAnalysis(analysis(dates));
			
			if(accessForbidRepository.countByIp(access.getIp()) > 0) {
				access.setForbidden(true);
			} else {
				access.setForbidden(false);
			}
		}
		return ipList;
	}
	
	@SuppressWarnings("unchecked")
	public List<HighFrequencyAccess> getTodayHighFrequencyAccessess(Integer currentPage, Integer pageSize) {
		String sql1 = "SELECT ip, ip_city AS ipCity, COUNT(ip) AS count FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW()) GROUP BY ip HAVING COUNT(ip) >= " + blogProperties.getDayAccessThreshold();
		Query query1 = entityManager.createNativeQuery(sql1);
		query1.setFirstResult(currentPage * pageSize);
        query1.setMaxResults(pageSize);
		query1.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(HighFrequencyAccess.class));
		List<HighFrequencyAccess> ipList = (List<HighFrequencyAccess>)query1.getResultList();
		for(HighFrequencyAccess access : ipList) {
			String sql2 = "SELECT access_time FROM tb_access WHERE ip='"+access.getIp()+"' AND TO_DAYS(access_time)=TO_DAYS(NOW());";
			Query query2 = entityManager.createNativeQuery(sql2);
			List<Date> dates = (List<Date>)query2.getResultList();
			access.setAnalysis(analysis(dates));
			
			if(accessForbidRepository.countByIp(access.getIp()) > 0) {
				access.setForbidden(true);
			} else {
				access.setForbidden(false);
			}
		}
		return ipList;
	}

	@SuppressWarnings("unchecked")
	public List<HighFrequencyAccess> getMonthHighFrequencyAccessess(Integer currentPage, Integer pageSize) {
		String sql1 = "SELECT ip, ip_city AS ipCity, COUNT(ip) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY ip HAVING COUNT(ip) >= " + blogProperties.getMonthAccessThreshold();
		Query query1 = entityManager.createNativeQuery(sql1);
		query1.setFirstResult(currentPage * pageSize);
        query1.setMaxResults(pageSize);
		query1.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(HighFrequencyAccess.class));
		List<HighFrequencyAccess> ipList = (List<HighFrequencyAccess>)query1.getResultList();
		for(HighFrequencyAccess access : ipList) {
			String sql2 = "SELECT access_time FROM tb_access WHERE ip='"+access.getIp()+"' AND DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m');";
			Query query2 = entityManager.createNativeQuery(sql2);
			List<Date> dates = (List<Date>)query2.getResultList();
			access.setAnalysis(analysis(dates));
			
			if(accessForbidRepository.countByIp(access.getIp()) > 0) {
				access.setForbidden(true);
			} else {
				access.setForbidden(false);
			}
		}
		return ipList;
	}
	
	@SuppressWarnings("unchecked")
	public List<HighFrequencyAccess> getYearHighFrequencyAccessess(Integer currentPage, Integer pageSize) {
		String sql1 = "SELECT ip, ip_city AS ipCity, COUNT(ip) AS count FROM tb_access WHERE YEAR(access_time)=YEAR(NOW()) GROUP BY ip HAVING COUNT(ip) >= " + blogProperties.getYearAccessThreshold();
		Query query1 = entityManager.createNativeQuery(sql1);
		query1.setFirstResult(currentPage * pageSize);
        query1.setMaxResults(pageSize);
		query1.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(HighFrequencyAccess.class));
		List<HighFrequencyAccess> ipList = (List<HighFrequencyAccess>)query1.getResultList();
		for(HighFrequencyAccess access : ipList) {
			String sql2 = "SELECT access_time FROM tb_access WHERE ip='"+access.getIp()+"' AND YEAR(access_time)=YEAR(NOW());";
			Query query2 = entityManager.createNativeQuery(sql2);
			List<Date> dates = (List<Date>)query2.getResultList();
			access.setAnalysis(analysis(dates));
			
			if(accessForbidRepository.countByIp(access.getIp()) > 0) {
				access.setForbidden(true);
			} else {
				access.setForbidden(false);
			}
		}
		return ipList;
	}
	
	/**
	 * 获取高频访问IP的数量
	 */
	public Integer getTotalHighFrequencyAccessessCount() {
		String sql = "SELECT COUNT(1) FROM (SELECT DISTINCT(ip) FROM tb_access GROUP BY ip HAVING COUNT(ip) >= "+ blogProperties.getAllAccessThreshold()+") AS ips;";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getTodayHighFrequencyAccessessCount() {
		String sql = "SELECT COUNT(1) FROM (SELECT DISTINCT(ip) FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW()) GROUP BY ip HAVING COUNT(ip) >= "+ blogProperties.getDayAccessThreshold()+") AS ips;";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getMonthHighFrequencyAccessessCount() {
		String sql = "SELECT COUNT(1) FROM (SELECT DISTINCT(ip) FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY ip HAVING COUNT(ip) >= "+ blogProperties.getMonthAccessThreshold()+") AS ips;";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getYearHighFrequencyAccessessCount() {
		String sql = "SELECT COUNT(1) FROM (SELECT DISTINCT(ip) FROM tb_access WHERE YEAR(access_time)=YEAR(NOW()) GROUP BY ip HAVING COUNT(ip) >= "+ blogProperties.getYearAccessThreshold()+") AS ips;";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	/**
	 * 根据某个IP的访问时间列表，分时段分析是否人机
	 *  
	 * @author lvchao 2018年9月17日
	 * @param dates
	 * @return
	 */
	private Map<String, Object> analysis(List<Date> dates) {
		Map<String, Object> map = new HashMap<String, Object>();
		Result[] results = new Result[12];
		for(int i =0; i< results.length; i++) {
			results[i] = new Result();
		}
		
		for (Date date : dates) {
			int start = 0, end = 1;
			String startTime = "", endTime = "";
			for (int i = 0; i < results.length; i++) {
				if (start < 10) {
					startTime = "0" + start + ":00:00";
					endTime = "0" + end + ":59:59";
				} else {
					startTime = start + ":00:00";
					endTime = end + ":59:59";
				}
				if (CommonUtils.isInPeriod(date, startTime, endTime)) {
					results[i].setCount(results[i].getCount() + 1);
					results[i].getDateSet().add(date);
				}
				start += 2;
				end += 2;
			}
		}
		
		int start = 0, end = 1;
		for (int i = 0; i < results.length; i++) {
			results[i].setAvgInterval(getAvgInterval(results[i].getDateSet()));
			results[i].setIsRobot(judgeIsRobot(results[i].getAvgInterval(), results[i].getCount()));
			if (start < 10) {
				map.put("0" + start + ":00:00-0" + end + ":59:59", results[i]);
			} else {
				map.put(start + ":00:00-" + end + ":59:59", results[i]);
			}
			start += 2;
			end += 2;
		}
		return map;
	}
	
	private float getAvgInterval(Set<Date> dateSet) {
		if(dateSet != null && dateSet.size() > 0) {
			Object[] dates = dateSet.toArray();
			float sum = 0.0f;
			for (int i = 0; i < dates.length - 1; i++) {
				sum += Math.abs(((Date)dates[i+1]).getTime() - ((Date)dates[i]).getTime());
			}
			return sum / dates.length / 1000;
		}
		return 0.0f;
	}
	
	private boolean judgeIsRobot(float avgInterval, int count) {
		int window = Integer.parseInt(blogProperties.getAccessWindow());
		int interval = Integer.parseInt(blogProperties.getAvgInterval());
		
		if(count > window && avgInterval < interval) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 获取浏览记录
	 */
	@SuppressWarnings("unchecked")
	public List<Access> getTotalScan(Integer currentPage, Integer pageSize) {
		String sql = "SELECT * FROM tb_access";
		Query query = entityManager.createNativeQuery(sql, Access.class);
		query.setFirstResult(currentPage * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Access> getTodayScan(Integer currentPage, Integer pageSize) {
		String sql = "SELECT * FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW())";
		Query query = entityManager.createNativeQuery(sql, Access.class);
		query.setFirstResult(currentPage * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Access> getMonthScan(Integer currentPage, Integer pageSize) {
		String sql = "SELECT * FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')";
		Query query = entityManager.createNativeQuery(sql, Access.class);
		query.setFirstResult(currentPage * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
	}
	@SuppressWarnings("unchecked")
	public List<Access> getYearScan(Integer currentPage, Integer pageSize) {
		String sql = "SELECT * FROM tb_access WHERE YEAR(access_time)=YEAR(NOW())";
		Query query = entityManager.createNativeQuery(sql, Access.class);
		query.setFirstResult(currentPage * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
	}
	
	
	/**
	 * 浏览量
	 */
	public Integer getTotalScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getTodayScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getTodayScanDetails() {
		String sql = "SELECT HOUR(access_time)+1 AS date,COUNT(id) AS count FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW()) GROUP BY HOUR(access_time) ORDER BY HOUR(access_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getYestodayScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access WHERE TO_DAYS(NOW())-TO_DAYS(access_time)=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYestodayScanDetails() {
		String sql = "SELECT HOUR(access_time)+1 AS date,COUNT(id) AS count FROM tb_access WHERE TO_DAYS(NOW())-TO_DAYS(access_time)=1 GROUP BY HOUR(access_time) ORDER BY HOUR(access_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getMonthScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getMonthScanDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%d') AS date,COUNT(id) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY DATE_FORMAT(access_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), false);
	}
	
	public Integer getLastMonthScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(access_time,'%Y%m'))=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastMonthScanDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%d') AS date,COUNT(id) AS count FROM tb_access WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(access_time,'%Y%m'))=1 GROUP BY DATE_FORMAT(access_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), true);
	}
	
	public Integer getYearScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access WHERE YEAR(access_time)=YEAR(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYearScanDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%m') AS date,COUNT(id) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y')=DATE_FORMAT(NOW(),'%Y') GROUP BY DATE_FORMAT(access_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	public Integer getLastYearScanCount() {
		String sql = "SELECT COUNT(id) FROM tb_access WHERE YEAR(access_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR))";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastYearScanDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%m') AS date,COUNT(id) AS count FROM tb_access WHERE YEAR(access_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR)) GROUP BY DATE_FORMAT(access_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	
	/**
	 * 访客
	 */
	public Integer getTotalVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getTodayVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getTodayVisitorDetails() {
		String sql = "SELECT HOUR(access_time)+1 AS date,COUNT(DISTINCT cookie_flag) AS count FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW()) GROUP BY HOUR(access_time) ORDER BY HOUR(access_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getYestodayVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access WHERE TO_DAYS(NOW())-TO_DAYS(access_time)=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYestodayVisitorDetails() {
		String sql = "SELECT HOUR(access_time)+1 AS date,COUNT(DISTINCT cookie_flag) AS count FROM tb_access WHERE TO_DAYS(NOW())-TO_DAYS(access_time)=1 GROUP BY HOUR(access_time) ORDER BY HOUR(access_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getMonthVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getMonthVisitorDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%d') AS date,COUNT(DISTINCT cookie_flag) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY DATE_FORMAT(access_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), false);
	}
	
	public Integer getLastMonthVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(access_time,'%Y%m'))=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastMonthVisitorDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%d') AS date,COUNT(DISTINCT cookie_flag) AS count FROM tb_access WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(access_time,'%Y%m'))=1 GROUP BY DATE_FORMAT(access_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), true);
	}
	
	public Integer getYearVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access WHERE YEAR(access_time)=YEAR(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYearVisitorDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%m') AS date,COUNT(DISTINCT cookie_flag) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y')=DATE_FORMAT(NOW(),'%Y') GROUP BY DATE_FORMAT(access_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	public Integer getLastYearVisitorCount() {
		String sql = "SELECT COUNT(DISTINCT cookie_flag) FROM tb_access WHERE YEAR(access_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR))";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastYearVisitorDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%m') AS date,COUNT(DISTINCT cookie_flag) AS count FROM tb_access WHERE YEAR(access_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR)) GROUP BY DATE_FORMAT(access_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	
	/**
	 * IP
	 */
	public Integer getTotalIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getTodayIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getTodayIPDetails() {
		String sql = "SELECT HOUR(access_time)+1 AS date,COUNT(DISTINCT ip) AS count FROM tb_access WHERE TO_DAYS(access_time)=TO_DAYS(NOW()) GROUP BY HOUR(access_time) ORDER BY HOUR(access_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getYestodayIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access WHERE TO_DAYS(NOW())-TO_DAYS(access_time)=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYestodayIPDetails() {
		String sql = "SELECT HOUR(access_time)+1 AS date,COUNT(DISTINCT ip) AS count FROM tb_access WHERE TO_DAYS(NOW())-TO_DAYS(access_time)=1 GROUP BY HOUR(access_time) ORDER BY HOUR(access_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getMonthIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getMonthIPDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%d') AS date,COUNT(DISTINCT ip) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY DATE_FORMAT(access_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), false);
	}
	
	public Integer getLastMonthIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(access_time,'%Y%m'))=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastMonthIPDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%d') AS date,COUNT(DISTINCT ip) AS count FROM tb_access WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(access_time,'%Y%m'))=1 GROUP BY DATE_FORMAT(access_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), true);
	}
	
	public Integer getYearIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access WHERE YEAR(access_time)=YEAR(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYearIPDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%m') AS date,COUNT(DISTINCT ip) AS count FROM tb_access WHERE DATE_FORMAT(access_time,'%Y')=DATE_FORMAT(NOW(),'%Y') GROUP BY DATE_FORMAT(access_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	public Integer getLastYearIPCount() {
		String sql = "SELECT COUNT(DISTINCT ip) FROM tb_access WHERE YEAR(access_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR))";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastYearIPDetails() {
		String sql = "SELECT DATE_FORMAT(access_time,'%m') AS date,COUNT(DISTINCT ip) AS count FROM tb_access WHERE YEAR(access_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR)) GROUP BY DATE_FORMAT(access_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	
	/**
	 * 评论
	 */
	public Integer getTotalCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	public Integer getTodayCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment WHERE TO_DAYS(publish_time)=TO_DAYS(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getTodayCommentDetails() {
		String sql = "SELECT HOUR(publish_time)+1 AS date,COUNT(id) AS count FROM tb_comment WHERE TO_DAYS(publish_time)=TO_DAYS(NOW()) GROUP BY HOUR(publish_time) ORDER BY HOUR(publish_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getYestodayCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment WHERE TO_DAYS(NOW())-TO_DAYS(publish_time)=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYestodayCommentDetails() {
		String sql = "SELECT HOUR(publish_time)+1 AS date,COUNT(id) AS count FROM tb_comment WHERE TO_DAYS(NOW())-TO_DAYS(publish_time)=1 GROUP BY HOUR(publish_time) ORDER BY HOUR(publish_time) ASC";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processDay(nativeQuery.getResultList());
	}
	
	public Integer getMonthCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment WHERE DATE_FORMAT(publish_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getMonthCommentDetails() {
		String sql = "SELECT DATE_FORMAT(publish_time,'%d') AS date,COUNT(id) AS count FROM tb_comment WHERE DATE_FORMAT(publish_time,'%Y%m')=DATE_FORMAT(CURDATE(),'%Y%m') GROUP BY DATE_FORMAT(publish_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), false);
	}
	
	public Integer getLastMonthCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(publish_time,'%Y%m'))=1";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastMonthCommentDetails() {
		String sql = "SELECT DATE_FORMAT(publish_time,'%d') AS date,COUNT(id) AS count FROM tb_comment WHERE PERIOD_DIFF(DATE_FORMAT(NOW(),'%Y%m'),DATE_FORMAT(publish_time,'%Y%m'))=1 GROUP BY DATE_FORMAT(publish_time,'%d')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processMonth(nativeQuery.getResultList(), true);
	}
	
	public Integer getYearCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment WHERE YEAR(publish_time)=YEAR(NOW())";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getYearCommentDetails() {
		String sql = "SELECT DATE_FORMAT(publish_time,'%m') AS date,COUNT(id) AS count FROM tb_comment WHERE DATE_FORMAT(publish_time,'%Y')=DATE_FORMAT(NOW(),'%Y') GROUP BY DATE_FORMAT(publish_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	public Integer getLastYearCommentCount() {
		String sql = "SELECT COUNT(id) FROM tb_comment WHERE YEAR(publish_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR))";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		return Integer.parseInt(nativeQuery.getSingleResult().toString());
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<Object, Object> getLastYearCommentDetails() {
		String sql = "SELECT DATE_FORMAT(publish_time,'%m') AS date,COUNT(id) AS count FROM tb_comment WHERE YEAR(publish_time)=YEAR(DATE_SUB(NOW(),INTERVAL 1 YEAR)) GROUP BY DATE_FORMAT(publish_time,'%m')";
		Query nativeQuery = entityManager.createNativeQuery(sql);
		nativeQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(AccessObject.class));
		return processYear(nativeQuery.getResultList());
	}
	
	
	/**
	 * 对统计SQL的结果集进行强制转换，否则会出问题
	 *  
	 * @author lvchao 2018年9月17日
	 * @param list
	 * @return
	 */
	public HashMap<Object, Object> processDay(List<AccessObject> list) {
		HashMap<Object, Object> map = new HashMap<>();
		for(int i=0; i<=24; i++) {
			map.put(i, 0);
		}
		for(AccessObject vo : list) {
			map.put(Integer.parseInt(vo.getDate().toString()), Integer.parseInt(vo.getCount().toString()));
		}
		return map;
	}
	
	public HashMap<Object, Object> processMonth(List<AccessObject> list, boolean isLastMonth) {
		Calendar cal = Calendar.getInstance();
		if(isLastMonth) {
			cal.add(Calendar.MONTH, -1);//上个月
		}
		cal.set(Calendar.DATE, 1);//把日期设置为当月第一天
	    cal.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
		int days = cal.get(Calendar.DATE);
		HashMap<Object, Object> map = new HashMap<>();
		for(int i=1; i<=days; i++) {
			map.put(i, 0);
		}
		for(AccessObject vo : list) {
			map.put(Integer.parseInt(vo.getDate().toString()), Integer.parseInt(vo.getCount().toString()));
		}
		return map;
	}
	
	public HashMap<Object, Object> processYear(List<AccessObject> list) {
		HashMap<Object, Object> map = new HashMap<>();
		for(int i=1; i<=12; i++) {
			map.put(i, 0);
		}
		for(AccessObject vo : list) {
			map.put(Integer.parseInt(vo.getDate().toString()), Integer.parseInt(vo.getCount().toString()));
		}
		return map;
	}
	
}

