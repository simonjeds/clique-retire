package com.clique.retire.client.rest;

import com.clique.retire.dto.ConexaoDeliveryLoginDTO;
import com.clique.retire.dto.ConexaoDeliveryTokenDTO;
import com.clique.retire.dto.DadosMotociclistaDTO;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers({
  "Content-Type: application/json",
  "Accept: application/json"
})
public interface ConexaoDeliveryClient {

  @RequestLine("POST /auth/auth")
  ConexaoDeliveryTokenDTO auth(ConexaoDeliveryLoginDTO loginDTO);

  @Headers({ "Authorization: {token} "})
  @RequestLine("GET /integration/pedidos/finalizarpedido/{numeroPedido}")
  void finalizarRotaPedido(@Param("numeroPedido") Integer numeroPedido, @Param("token") String token);

  @Headers({ "Authorization: {token} "})
  @RequestLine("GET /integration/cooperado/buscarMotociclistaPorPedido/{numeroPedido}")
  DadosMotociclistaDTO obterDadosMotociclista(
    @Param("numeroPedido") Integer numeroPedido, @Param("token") String token
  );

}
