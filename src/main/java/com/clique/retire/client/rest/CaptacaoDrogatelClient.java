package com.clique.retire.client.rest;

import com.clique.retire.dto.SolicitacaoCaptacaoDTO;
import com.google.gson.JsonObject;

import feign.Headers;
import feign.RequestLine;

public interface CaptacaoDrogatelClient {

	@RequestLine("POST captarReceita")
	@Headers({ "Content-Type: application/json" })
	public JsonObject salvarCaptacao(SolicitacaoCaptacaoDTO solicitacaoCaptacaoDTO);

}
