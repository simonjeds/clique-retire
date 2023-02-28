package com.clique.retire.client.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.clique.retire.dto.CaptacaoLoteDTO;
import com.google.gson.JsonObject;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface CaptacaoClient {

	@RequestLine("GET /sv-carrinho/captacao/retorno-captacao/{idPedido}")
	@Headers({ "Authorization: Bearer {token}", "Content-Type:application/json" })
	public JsonObject retornarCaptacao(@Param("idPedido") Long idPedido, @Param("token") String token);

	@RequestLine("POST /sv-carrinho/captacao/capitacao-lote-controlados")
	@Headers({ "Authorization: Bearer {token}", "Content-Type:application/json" })
	public JsonObject captacaoLote(@RequestBody List<CaptacaoLoteDTO> capitacoes, @Param("token") String token);

}
