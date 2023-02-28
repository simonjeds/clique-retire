package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.PedidoClient;
import com.clique.retire.dto.PedidoIdentificadorUnicoDTO;

public class PedidoClientImpl extends AbstractRestClient implements PedidoClient {

  public PedidoClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public PedidoIdentificadorUnicoDTO buscarIdPedidoDrogatel(String uniqueIdentifier) {
    String url = "api/Pedido?identificadorUnico={uniqueIdentifier}";
    return this.getRestTemplate().getForObject(url, PedidoIdentificadorUnicoDTO.class, uniqueIdentifier);
  }

}
