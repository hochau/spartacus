package com.baoxue.spartacus.config;

import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 
 * @author lvchao
 * @createtime 2018年4月13日 下午4:00:34
 */
@Configuration
public class BlogBeanConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogBeanConfig.class);

    @Autowired
    private BlogProperties blogProperties;


    @Bean("myAsync")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("myAsync-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
    
    /**
     * 忽略对象空值字段的JSON序列化问题
     *  
     * @author lvchao 2018年8月31日
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }


    public COSClient getCosClient() {
        // 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(blogProperties.getSecretId(), blogProperties.getSecretKey());
        // 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(blogProperties.getBucketRegion()));
        // 生成cos客户端
        return new COSClient(cred, clientConfig);
    }

    public TransportClient getEsClient() {
        TransportClient transportClient = null;

        try {
            // 配置信息
            Settings settings = Settings.builder()
                    /**
                     * 设置client.transport.sniff为true来使客户端去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中，
                     * 这样做的好处是一般你不用手动设置集群里所有集群的ip到连接客户端，它会自动帮你添加，并且自动发现新加入集群的机器。
                     *
                     * 注意：当ES服务器监听使用内网服务器IP而访问使用外网IP时，不要使用client.transport.sniff为true，
                     * 在自动发现时会使用内网IP进行通信，导致无法连接到ES服务器，而直接使用addTransportAddress方法进行指定ES服务器。
                     */
                    .put("client.transport.sniff", true) // 集群嗅探机制
                    .put("cluster.name", blogProperties.getClusterName())
                    .build();

            // 使用自定义配置
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(blogProperties.getHost()), Integer.valueOf(blogProperties.getPort()));
            transportClient = new PreBuiltTransportClient(settings).addTransportAddresses(transportAddress);

        } catch (Exception e) {
            LOGGER.error("Elasticsearch TransportClient Create Error!", e);
        }

        return transportClient;
    }

}