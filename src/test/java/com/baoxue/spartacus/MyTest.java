package com.baoxue.spartacus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Description
 * @Author C
 * @Date 2019/8/26 5:25
 **/
public class MyTest {
    public static void main(String[] args) throws IOException{
        String s = isImagesTrue("https://tret-1251733385.cos.ap-chengdu.myqcloud.com/image/34223432889049088-微信图片_20180620232950.jpg");
        System.out.println(s);
    }

    public static void testUrlWithTimeOut(String urlString, int timeOutMillSeconds){
        long lo = System.currentTimeMillis();
        URL url;
        try {
            url = new URL(urlString);
            URLConnection co =  url.openConnection();
            co.setConnectTimeout(timeOutMillSeconds);
            co.connect();
            System.out.println("连接可用");
        } catch (Exception e1) {
            System.out.println("连接打不开!");
            url = null;
        }
        System.out.println(System.currentTimeMillis()-lo);
    }

    public static String isImagesTrue(String posturl) throws IOException {
        URL url = new URL(posturl);
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        urlcon.setRequestMethod("POST");
        urlcon.setRequestProperty("Content-type",
                "application/x-www-form-urlencoded");
        if (urlcon.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println(HttpURLConnection.HTTP_OK + posturl
                    + ":posted ok!");
            return "200";
        } else {
            System.out.println(urlcon.getResponseCode() + posturl
                    + ":Bad post...");
            return "404";
        }
    }
}
