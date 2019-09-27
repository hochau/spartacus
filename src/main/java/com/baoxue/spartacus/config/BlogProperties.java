package com.baoxue.spartacus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@PropertySource("classpath:blogconfig.properties")
public class BlogProperties {
	//管理员信息
    @Value("${admin_username}")
    private String adminUsername;
    @Value("${admin_password}")
    private String adminPassword;
	@Value("${admin_nickname}")
    private String adminNickname;
    @Value("${admin_headImg}")
    private String adminHeadImg;
    
	//COS配置（机密）
    @Value("${cos_secretId}")
    private String secretId;
    @Value("${cos_secretKey}")
    private String secretKey;
    @Value("${cos_bucketRegion}")
    private String bucketRegion;
    @Value("${cos_bucketName}")
    private String bucketName;
    @Value("${cos_basePath}")
    private String basePath;
    @Value("${cos_baseUrl}")
    private String baseUrl;
    @Value("${cos_defaultUrl}")
    private String defaultUrl;
    @Value("${cos_rootDirPaths}")
    private String rootDirPaths;

    // ES相关配置
    @Value("${es_host}")
    private String host;
    @Value("${es_port}")
    private String port;
    @Value("${es_clusterName}")
    private String clusterName;

    // 百度地图
    @Value("${baidu_ak}")
    private String baiduAk;
    @Value("${baidu_url}")
    private String baiduUrl;

    //聚合数据-全国天气预报
    @Value("${weather_url}")
    private String weatherUrl;
    @Value("${weather_url_gps}")
    private String weatherUrlGps;
    @Value("${weather_key}")
    private String weatherKey;
    
    //高频访问IP访问次数控制
    @Value("${access_dayAccessThreshold}")
    private String dayAccessThreshold;
    @Value("${access_monthAccessThreshold}")
    private String monthAccessThreshold;
    @Value("${access_yearAccessThreshold}")
    private String yearAccessThreshold;
    @Value("${access_allAccessThreshold}")
    private String allAccessThreshold;
    
    //高频访问IP访问次数控制
    @Value("${access_accessWindow}")
    private String accessWindow;
    @Value("${access_avgInterval}")
    private String avgInterval;
    
    //调用远程python脚本服务的认证码
    @Value("${api_appid}")
    private String apiAppid;

    //websocket
    @Value("${wb_endpoint}")
    private String wbEndpoint;
    @Value("${wb_sub_path_prefixes}")
    private String wbSubPathPrefixes;
    @Value("${wb_server_path_prefixes}")
    private String wbServerPathPrefixes;
    
}
