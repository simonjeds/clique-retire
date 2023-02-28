package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.CaptacaoDrogatelClient;
import com.clique.retire.dto.SolicitacaoCaptacaoDTO;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;

public class CaptacaoDrogatelClientImpl extends AbstractRestClient implements CaptacaoDrogatelClient {

  public CaptacaoDrogatelClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public JsonObject salvarCaptacao(SolicitacaoCaptacaoDTO solicitacaoCaptacaoDTO) {
    String url = "captarReceita";
    HttpEntity<SolicitacaoCaptacaoDTO> httpEntity = new HttpEntity<>(solicitacaoCaptacaoDTO, this.getHttpHeaders());
    return this.getRestTemplate().postForObject(url, httpEntity, JsonObject.class);
  }

}
