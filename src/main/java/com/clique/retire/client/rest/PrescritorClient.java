package com.clique.retire.client.rest;

import com.clique.retire.service.drogatel.PrescritorService;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface PrescritorClient {
	
	@RequestLine("GET /sv-carrinho/medico?conselho={conselho}&numeroRegistro={numeroRegistro}&ufRegistro={ufRegistro}")
	@Headers({ "Authorization: Bearer {token}", "Content-Type:application/json" })
	PrescritorService.MedicoResponseDTO getMedico(@Param("conselho") String  conselho, @Param("ufRegistro") String  ufRegistro, @Param("numeroRegistro") Integer  numeroRegistro, @Param("token") String  token);
	
}
