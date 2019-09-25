package com.baoxue.spartacus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer{

	@Autowired
	BlogProperties blogProperties;
	
	/**
	 * 注册端点（类似于手机信号基站），发布或者订阅消息的时候需要连接此端点
	 * setAllowedOrigins 非必须，*表示允许任何域进行连接
	 * withSockJS  表示开启sockejs支持
	 */
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		String[] points = blogProperties.getWbEndpoint().split(",");
		//注意 如果必须登录后才能建立WebSocket连接，下面的这个url不要在spring security中配置允许访问
		registry.addEndpoint(points) //添加STOMP协议的端点，这个是HTTP URL，提供给WebSocket或SockJS客户端访问的地址
				.setAllowedOrigins("*") // 添加允许跨域访问
				.withSockJS(); //如果前台使用sockJs，此处没有设置，websocket报404错误
	}

	/**
	 * 配置消息代理(中介)
	 * enableSimpleBroker 服务端推送给客户端的路径前缀
	 * setApplicationDestinationPrefixes  客户端发送数据给服务器端的路劲前缀
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//服务端给客户端发消息的地址的前缀
		String[] brokers = blogProperties.getWbSubPathPrefixes().split(",");
		registry.enableSimpleBroker(brokers);

		//客户端给服务端发消息的地址的前缀
		String[] prefixes = blogProperties.getWbServerPathPrefixes().split(",");
		registry.setApplicationDestinationPrefixes(prefixes);
	}


}


