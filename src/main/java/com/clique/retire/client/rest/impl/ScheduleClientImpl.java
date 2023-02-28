package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.ScheduleClient;

public class ScheduleClientImpl extends AbstractRestClient implements ScheduleClient {

  public ScheduleClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public void atualizarCorSinalizador(String corHexadecimal, Integer filial) {
    String url = "checklist/atualizar-cor-sinalizador?corHexadecimal={corHexadecimal}&filial={filial}";
    this.getRestTemplate().getForObject(url, Object.class, corHexadecimal, filial);
  }

  @Override
  public void atualizarLedFilial(Integer filial) {
    String url = "pedido/reportar-novo-pedido?codigoFilial={filial}";
    this.getRestTemplate().getForObject(url, Object.class, filial);
  }

}
