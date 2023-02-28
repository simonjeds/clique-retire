package com.clique.retire.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("api.conexao-delivery")
public class ConexaoDeliveryConfigurationProperties {

  private String url;
  private String usuario;
  private String senha;

  private String urlIntegradorExpedicao;

}
