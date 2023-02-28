package com.clique.retire.client.rest;

import com.clique.retire.dto.PedidoIdentificadorUnicoDTO;

import feign.Param;
import feign.RequestLine;

public interface PedidoClient {

	@RequestLine("GET api/Pedido?identificadorUnico={identificadorUnico}")
	public PedidoIdentificadorUnicoDTO buscarIdPedidoDrogatel(@Param("identificadorUnico") String identificadorUnico); 
}
