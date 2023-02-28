package com.clique.retire.service.drogatel;

import com.clique.retire.client.rest.ConexaoDeliveryClient;
import com.clique.retire.client.rest.IntegradorExpedicaoClient;
import com.clique.retire.config.properties.ConexaoDeliveryConfigurationProperties;
import com.clique.retire.dto.ConexaoDeliveryLoginDTO;
import com.clique.retire.dto.ConexaoDeliveryTokenDTO;
import com.clique.retire.dto.DadosMotociclistaDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.util.FeignUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ConexaoDeliveryService {

  private ConexaoDeliveryClient conexaoDeliveryClient;
  private IntegradorExpedicaoClient integradorExpedicaoClient;
  private final ConexaoDeliveryConfigurationProperties conexaoDeliveryConfigurationProperties;

  @PostConstruct
  private void init() {
    this.conexaoDeliveryClient = FeignUtil.getConexaoDeliveryClient(conexaoDeliveryConfigurationProperties.getUrl());
    this.integradorExpedicaoClient = FeignUtil.getIntegradorExpedicaoClient(
      conexaoDeliveryConfigurationProperties.getUrlIntegradorExpedicao()
    );
  }

  public String auth() {
    ConexaoDeliveryLoginDTO loginDTO = new ConexaoDeliveryLoginDTO();
    loginDTO.setLogin(conexaoDeliveryConfigurationProperties.getUsuario());
    loginDTO.setPassword(conexaoDeliveryConfigurationProperties.getSenha());
    ConexaoDeliveryTokenDTO tokenDTO = this.conexaoDeliveryClient.auth(loginDTO);
    return tokenDTO.getToken();
  }

  public void finalizarRotaPedido(Integer numeroPedido) {
    String token = this.auth();
    this.conexaoDeliveryClient.finalizarRotaPedido(numeroPedido, token);
    this.integradorExpedicaoClient.finalizarRota(numeroPedido, FasePedidoEnum.NAO_ENTREGUE.name());
  }

  public DadosMotociclistaDTO obterDadosMotociclista(Integer numeroPedido) {
    String token = this.auth();
    return this.conexaoDeliveryClient.obterDadosMotociclista(numeroPedido, token);
  }

}
