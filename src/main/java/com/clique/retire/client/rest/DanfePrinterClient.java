package com.clique.retire.client.rest;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

public interface DanfePrinterClient {

  @RequestLine("GET /print/by-nfe-key/{nfeKey}")
  @Headers({ "Accept: application/pdf" })
  Response gerarDanfePelaChaveNota(@Param("nfeKey") String chaveNota);

}
