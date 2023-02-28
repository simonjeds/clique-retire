package com.clique.retire.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("pbm")
public class PBMConfigurationProperties {

  private String url;
  private String clientId;
  private String accessToken;

}
