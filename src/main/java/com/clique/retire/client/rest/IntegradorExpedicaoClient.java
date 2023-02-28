package com.clique.retire.client.rest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers({
  "Content-Type: application/json",
  "Accept: application/json"
})
public interface IntegradorExpedicaoClient {

  @RequestLine("GET /cooperativas/finalizarRotaPedido/{idPedido}?nomeStatus={status}")
  void finalizarRota(@Param("idPedido") Integer idPedido, @Param("status") String status);

}
