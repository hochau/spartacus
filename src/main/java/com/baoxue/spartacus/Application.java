package com.baoxue.spartacus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableAsync
@EnableTransactionManagement // 开启注解事务管理
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
