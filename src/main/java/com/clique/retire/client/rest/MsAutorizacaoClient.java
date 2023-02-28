package com.clique.retire.client.rest;

import com.clique.retire.dto.AutorizacaoResponseDTO;
import feign.Param;
import feign.RequestLine;

public interface MsAutorizacaoClient {

    @RequestLine("POST usuarios/credenciais?credenciais={credenciais}")
    AutorizacaoResponseDTO gerarAutorizacao(@Param("credenciais") String credenciais);

}