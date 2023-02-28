package com.clique.retire;

import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableFeignClients
@EnableSwagger2
@Slf4j
public class Application {

	@PostConstruct
	private void initTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
		log.info("Spring boot application running in UTC timezone : {}" , new Date());
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
