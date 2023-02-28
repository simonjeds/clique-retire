package com.clique.retire.client.rest;

import com.clique.retire.dto.BaseResponseDTO;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface EmitirEtiquetaClient {
	
	@Headers("Content-Type: application/json")	
	@RequestLine("POST /impressao/{filial}/zpl")
	public String gerarEtiqueta(@Param("filial") Integer  idLoja,  BaseResponseDTO responseDTO); 
	
}
