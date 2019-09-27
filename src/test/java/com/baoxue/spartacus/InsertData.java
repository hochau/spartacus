//package com.baoxue.spartacus;
//
//import com.baoxue.spartacus.pojo.Access;
//import com.baoxue.spartacus.utils.Snowflake;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.transaction.TransactionConfiguration;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
///**
// * @Description
// * @Author C
// * @Date 2019/9/24 18:38
// **/
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@TransactionConfiguration(defaultRollback = false)
//public class InsertData {
//
//    @PersistenceContext
//    protected EntityManager entityManager;
//
//    public static Random random = new Random();
//
//    @Test
//    @Transactional
//    public void insertAccessData() {
//
//        Map<String, String> map = new HashMap<>();
//        map.put("深圳", "192.168.3.1");
//        map.put("上海", "192.168.3.2");
//        map.put("北京", "192.168.3.3");
//        map.put("天津", "192.168.3.4");
//        map.put("武汉", "192.168.3.5");
//        map.put("广州", "192.168.3.6");
//        map.put("长沙", "192.168.3.7");
//        map.put("南京", "192.168.3.8");
//        map.put("合肥", "192.168.3.9");
//        map.put("西安", "192.168.3.10");
//        map.put("贵阳", "192.168.3.11");
//        map.put("昆明", "192.168.3.12");
//        map.put("湘潭", "192.168.3.13");
//        map.put("株洲", "192.168.3.14");
//        map.put("邵阳", "192.168.3.15");
//        map.put("衡阳", "192.168.3.16");
//        map.put("岳阳", "192.168.3.17");
//        map.put("三亚", "192.168.3.18");
//        map.put("成都", "192.168.3.19");
//        map.put("厦门", "192.168.3.20");
//        map.put("新疆", "192.168.3.21");
//        map.put("安庆", "192.168.3.22");
//        map.put("芜湖", "192.168.3.23");
//        map.put("苏州", "192.168.3.24");
//        map.put("扬州", "192.168.3.25");
//        map.put("青岛", "192.168.3.26");
//        map.put("福州", "192.168.3.27");
//        map.put("重庆", "192.168.3.28");
//        map.put("汕头", "192.168.3.29");
//        map.put("汕尾", "192.168.3.30");
//
//
//        for (int i=844; i<1600; i++) {
//            Access a = new Access();
//            a.setId(Snowflake.generateId());
//            a.setAccessTime(randomDate("2019-09-27 00:00:00", "2019-09-27 23:59:59"));
//            a.setCookieFlag("cookie_"+i);
//            a.setGps("114.01952603,22.53987777");
//
//            String key = getRandomKeyFromMap(map);
//            a.setGpsAddress(key + "市XXX");
//            a.setIp(map.get(key));
//            a.setIpCity(key);
//
//            a.setUrl("http://spartacus.fm/xxx/yyy");
//            entityManager.persist(a);
//        }
//        entityManager.flush();
//        entityManager.clear();
//    }
//
//////////////////////////////////////////////////////////////////////////////
//    public static <K,V> K getRandomKeyFromMap(Map<K,V> map) {
//        int rn = getRandomInt(map.size());
//        int i = 0;
//        for (K key : map.keySet()) {
//            if(i==rn){
//                return key;
//            }
//            i++;
//        }
//        return null;
//    }
//
//    /**
//     * 获得一个[0,max)之间的整数。
//     * @param max
//     * @return
//     */
//    public static int getRandomInt(int max) {
//        return Math.abs(random.nextInt()) % max;
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////
//    private static Date randomDate(String beginDate, String endDate)
//    {
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date start = format.parse(beginDate);// 构造开始日期
//            Date end = format.parse(endDate);// 构造结束日期
//            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
//            /*if (start.getTime() >= end.getTime()) {
//                return null;
//            }*/
//            long date = random(start.getTime(), end.getTime());
//
//            return new Date(date);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private static long random(long begin, long end)
//    {
//        long rtn = begin + (long) (Math.random() * (end - begin));
//        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
//        if (rtn == begin || rtn == end) {
//            return random(begin, end);
//        }
//        return rtn;
//    }
//
//}
