package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.EmitirEtiquetaClient;
import com.clique.retire.dto.BaseResponseDTO;
import org.springframework.http.HttpEntity;

public class EmitirEtiquetaClientImpl extends AbstractRestClient implements EmitirEtiquetaClient {

  public EmitirEtiquetaClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public String gerarEtiqueta(Integer idLoja, BaseResponseDTO body) {
    String url = "/impressao/{filial}/zpl";
    HttpEntity<BaseResponseDTO> httpEntity = new HttpEntity<>(body, this.getHttpHeaders());
    return this.getRestTemplate().postForObject(url, httpEntity, String.class, idLoja);
  }

}
