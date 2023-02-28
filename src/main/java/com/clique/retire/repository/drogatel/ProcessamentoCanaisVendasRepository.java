package com.clique.retire.repository.drogatel;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.clique.retire.dto.ResponseValidarEnderecoDTO;

@FeignClient(name = "processamentoPlataformaVendasDelivery", url = "${cv.processamento.api}")
public interface ProcessamentoCanaisVendasRepository {
	
		@PostMapping(value = "valida-enderecos", consumes = MediaType.APPLICATION_JSON_VALUE)
		ResponseValidarEnderecoDTO validarEnderecoNotaFiscal(String numeroPedido);
	
}
