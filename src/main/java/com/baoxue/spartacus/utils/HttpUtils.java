package com.baoxue.spartacus.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @auth: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2017年8月21日上午9:49:11
 *
 * @desc: get请求、post请求、get请求下载图片
 */
public class HttpUtils {
	private static CloseableHttpClient httpClient = HttpClients.createDefault();

	private static RequestConfig config = RequestConfig.custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();

	
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	
	/**
	 * get请求，Json类型请求参数
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String doGetJson(String url, JSONObject json) throws Exception {
		CloseableHttpResponse response = null;
		
		try {
			// 遍历map,拼接请求参数
			URIBuilder uriBuilder = new URIBuilder(url);
			
			if(json != null) {
				Set<Entry<String, Object>> set = json.entrySet();
				for(Entry<String, Object> entry : set) {
					uriBuilder.setParameter(entry.getKey().toString(), entry.getValue().toString());
				}
			}
			
			// 声明 http get 请求
			HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
			// 装载配置信息
			httpGet.setConfig(config);
			// 发起请求
			response = httpClient.execute(httpGet);
			// 判断状态码是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				// 返回响应体的内容
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
		return null;
	}
	
	
	/**
	 * get请求，Map类型请求参数
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String doGetMap(String url, Map<String, Object> map) throws Exception {
		CloseableHttpResponse response = null;

		try {
			// 遍历map,拼接请求参数
			URIBuilder uriBuilder = new URIBuilder(url);
			if (map != null) {
				for (Entry<String, Object> entry : map.entrySet()) {
					uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
				}
			}
			// 声明 http get 请求
			HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
			// 装载配置信息
			httpGet.setConfig(config);
			// 发起请求
			response = httpClient.execute(httpGet);
			// 判断状态码是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				// 返回响应体的内容
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
		return null;
	}

	
	
	/**
	 * post请求，Map类型请求参数
	 * 
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static String doPostMap(String url, Map<String, Object> map) throws Exception {
		CloseableHttpResponse response = null;
		
		try {
			// 声明httpPost请求
			HttpPost httpPost = new HttpPost(url);
			// 加入配置信息
			httpPost.setConfig(config);

			// 判断map是否为空，不为空则进行遍历，封装from表单对象
			if (map != null) {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				for (Entry<String, Object> entry : map.entrySet()) {
					list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
				// 构造from表单对象
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "UTF-8");

				// 把表单放到post里
				httpPost.setEntity(urlEncodedFormEntity);
			}
			// 发起请求
			response = httpClient.execute(httpPost);
			
			// 关闭流
			EntityUtils.consumeQuietly(response.getEntity());
			
			// 返回响应值
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
		
		return null;
	}

	
	
	/**
	 * post请求，Json类型请求参数
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static String doPostJson(String url, JSONObject json) throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 声明httpPost请求
			HttpPost httpPost = new HttpPost(url);
			// 加入配置信息
			httpPost.setConfig(config);

			// 给httpPost设置JSON格式的参数
			if(json != null) {
				String reqParameter = null;
				// 1、防止正文里中文乱码  2、URLEncoder.encode()后空格变成了+，再替换成空格
				reqParameter = URLEncoder.encode(json.toString(), "UTF-8").replaceAll("\\+", "%20");
				StringEntity requestEntity = new StringEntity(reqParameter, "utf-8");
				httpPost.addHeader("Content-type","application/x-www-form-urlencoded; charset=UTF-8");
				httpPost.setEntity(requestEntity);
			}

			// 发起请求
			response = httpClient.execute(httpPost);
			
			// 返回响应值
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
		
		return null;
	}

	
	/**
	 * 下载图片
	 * 
	 * @param @param
	 *            url
	 * @param @return
	 * @param @throws
	 *            Exception
	 * @return: byte[]
	 */
	public static byte[] downloadImage(String url) throws Exception {
		
		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.setConfig(config);

			response = httpClient.execute(httpget);

			// 判断状态码是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				
				// 返回响应体的内容
				return EntityUtils.toByteArray(response.getEntity());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (response != null) {
					EntityUtils.consume(response.getEntity());
				}
			} catch (IOException e) {
				logger.warn("close response fail", e);
			}
		}
		
		return null;
	}
	
}