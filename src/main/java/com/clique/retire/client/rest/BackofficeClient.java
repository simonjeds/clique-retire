package com.clique.retire.client.rest;

import com.clique.retire.dto.BaseResponseDTO;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@Headers("api_key: psw#frameworkSwagger2017;")
public interface BackofficeClient {
	
	@RequestLine("GET pedido/token-entrega/{numeroPedidoEcommerce}")
	public BaseResponseDTO obterCodigoAberturaBox(@Param("numeroPedidoEcommerce") String numeroPedidoEcommerce);

	@RequestLine("GET armario/armarios/{numeroPedidoEcommerce}/token")
	BaseResponseDTO obterCodigoAberturaBoxSite(@Param("numeroPedidoEcommerce") String numeroPedidoEcommerce);

}
