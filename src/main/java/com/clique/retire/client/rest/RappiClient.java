package com.clique.retire.client.rest;

import com.clique.retire.dto.MarketplaceHandshakeResponseDTO;
import com.clique.retire.dto.PinMarketplaceDTO;
import feign.Param;
import feign.Response;
import org.springframework.http.MediaType;

import feign.Headers;
import feign.RequestLine;

@Headers({
	"api_key: psw#Framework_Araujo@2020",
	"Content-Type: " + MediaType.APPLICATION_JSON_VALUE,
	"Accept: " + MediaType.APPLICATION_JSON_VALUE
})
public interface RappiClient {

	@RequestLine("POST /api/v1/cliqueretire/iniciarRetirada/{codigoPedidoDrogatel}")
	void iniciarRetirada(@Param("codigoPedidoDrogatel") Integer codigoPedidoDrogatel);

	@RequestLine("POST /api/v1/cliqueretire/finalizarRetirada")
	Response finalizarRetirada(PinMarketplaceDTO pinMarketPlaceDTO);

	@RequestLine("POST /api/v1/cliqueretire/handshake")
	MarketplaceHandshakeResponseDTO handShake(PinMarketplaceDTO pinMarketPlaceDTO);
	
}
