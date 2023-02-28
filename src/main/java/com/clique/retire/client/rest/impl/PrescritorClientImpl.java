package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.PrescritorClient;
import com.clique.retire.service.drogatel.PrescritorService;
import com.google.gson.JsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class PrescritorClientImpl extends AbstractRestClient implements PrescritorClient {

  public PrescritorClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public PrescritorService.MedicoResponseDTO getMedico(String conselho, String ufRegistro, Integer numeroRegistro, String token) {
    String url = "/sv-carrinho/medico?conselho={conselho}&numeroRegistro={numeroRegistro}&ufRegistro={ufRegistro}";

    HttpEntity<JsonObject> httpEntity = new HttpEntity<>(this.getHttpHeaders(token));
    ResponseEntity<PrescritorService.MedicoResponseDTO> response = this.getRestTemplate().exchange(
      url, HttpMethod.GET, httpEntity, PrescritorService.MedicoResponseDTO.class, conselho, ufRegistro, numeroRegistro
    );
    return response.getBody();
  }

}
