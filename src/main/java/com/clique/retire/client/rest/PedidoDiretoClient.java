package com.clique.retire.client.rest;

import com.clique.retire.dto.PedidoUzDTO;

import feign.Param;
import feign.RequestLine;

public interface PedidoDiretoClient {

	@RequestLine("GET api/PedidoDireto/obterProdutosUZ/{codigoPedido}")
	public PedidoUzDTO buscarPedidosProdutosUz(@Param("codigoPedido") Integer codigoPedido); 
	
}
