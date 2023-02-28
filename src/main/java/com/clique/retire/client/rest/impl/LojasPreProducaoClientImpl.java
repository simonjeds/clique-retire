package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.LojasPreProducaoClient;
import feign.Response;

public class LojasPreProducaoClientImpl extends AbstractRestClient implements LojasPreProducaoClient {

  public LojasPreProducaoClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public Response lojaPreProducao() {
    // Método provisório apenas para seguir o contrato da interface
    return null;
  }

  //  @Override
  //  Quando mudar o LojasPreProducaoService para usar RestTemplate, usar este método
  public String lojaPreProducaoNOVO() {
    return this.getRestTemplate().getForObject("", String.class);
  }

}
