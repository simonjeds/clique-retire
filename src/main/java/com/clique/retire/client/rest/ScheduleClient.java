package com.clique.retire.client.rest;

import feign.Param;
import feign.RequestLine;

public interface ScheduleClient {
	
  @RequestLine("GET checklist/atualizar-cor-sinalizador?corHexadecimal={corHexadecimal}&filial={filial}")
  void atualizarCorSinalizador(@Param("corHexadecimal") String corHexadecimal, @Param("filial") Integer filial);

  @RequestLine("GET pedido/reportar-novo-pedido?codigoFilial={filial}")
  void atualizarLedFilial(@Param("filial") Integer filial);

}
