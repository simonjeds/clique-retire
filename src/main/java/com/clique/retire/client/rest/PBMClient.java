package com.clique.retire.client.rest;

import com.clique.retire.dto.PBMAutorizadorDTO;
import com.clique.retire.dto.PBMAutorizadorResponseDTO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.MediaType;

@Headers({
  "Content-Type: " + MediaType.APPLICATION_JSON_UTF8_VALUE,
  PBMClient.CLIENT_ID,
  PBMClient.ACCESS_TOKEN
})
public interface PBMClient {

  String CLIENT_ID = "client_id: {pbm_client_id}";
  String ACCESS_TOKEN = "access_token: {pbm_access_token}";

  @RequestLine("POST Autorizacao")
  PBMAutorizadorResponseDTO gerarAutorizacao(
    PBMAutorizadorDTO pbmAutorizadorDTO, @Param("pbm_client_id") String clientId,
    @Param("pbm_access_token") String accessToken
  );

}