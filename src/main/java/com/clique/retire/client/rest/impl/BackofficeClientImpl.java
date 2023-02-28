package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.BackofficeClient;
import com.clique.retire.dto.BaseResponseDTO;

public class BackofficeClientImpl extends AbstractRestClient implements BackofficeClient {

  public BackofficeClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public BaseResponseDTO obterCodigoAberturaBox(String numeroPedidoEcommerce) {
    String url = "pedido/token-entrega/{numeroPedidoEcommerce}";
    return this.getRestTemplate().getForObject(url, BaseResponseDTO.class, numeroPedidoEcommerce);
  }

  @Override
  public BaseResponseDTO obterCodigoAberturaBoxSite(String numeroPedidoEcommerce) {
    String url = "armario/armarios/{numeroPedidoEcommerce}/token";
    return this.getRestTemplate().getForObject(url, BaseResponseDTO.class, numeroPedidoEcommerce);
  }

}
