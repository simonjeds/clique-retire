package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.PedidoDiretoClient;
import com.clique.retire.dto.PedidoUzDTO;

public class PedidoDiretoClientImpl extends AbstractRestClient implements PedidoDiretoClient {

  public PedidoDiretoClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public PedidoUzDTO buscarPedidosProdutosUz(Integer orderCode) {
    String url = "api/PedidoDireto/obterProdutosUZ/{orderCode}";
    return this.getRestTemplate().getForObject(url, PedidoUzDTO.class, orderCode);
  }

}
