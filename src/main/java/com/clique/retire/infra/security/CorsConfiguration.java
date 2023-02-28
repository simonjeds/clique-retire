package com.clique.retire.infra.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * @author everton.gomes
 * Classe responsável por habilitar o acesso de outros dominios á aplicação.
 */
@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer  {
	
  @Override
  public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*").allowedOrigins("*");
  }
  
}