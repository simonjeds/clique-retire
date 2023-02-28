package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.CaptacaoClient;
import com.clique.retire.dto.CaptacaoLoteDTO;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CaptacaoClientImpl extends AbstractRestClient implements CaptacaoClient {

  public CaptacaoClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public JsonObject retornarCaptacao(Long idPedido, String token) {
    String url = "/sv-carrinho/captacao/retorno-captacao/{idPedido}";

    HttpEntity<JsonObject> httpEntity = new HttpEntity<>(this.getHttpHeaders(token));
    ResponseEntity<JsonObject> response = this.getRestTemplate()
      .exchange(url, HttpMethod.GET, httpEntity, JsonObject.class, idPedido);
    return response.getBody();
  }

  @Override
  public JsonObject captacaoLote(List<CaptacaoLoteDTO> captacoes, String token) {
    String url = "/sv-carrinho/captacao/capitacao-lote-controlados";
    HttpEntity<List<CaptacaoLoteDTO>> httpEntity = new HttpEntity<>(captacoes, this.getHttpHeaders(token));
    return this.getRestTemplate().postForObject(url, httpEntity, JsonObject.class);
  }

}
