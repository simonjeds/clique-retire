package com.clique.retire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.service.drogatel.LojasPreProducaoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Lojas PreProducao")
@RestController
@RequestMapping(value = "/rest/lojas-preproducao", produces = "application/json;charset=UTF-8")

public class LojasPreProducaoController extends BaseController {
	
	@Autowired
	private LojasPreProducaoService lojasPreProducaoService;

	@GetMapping
	@ApiOperation(value = "Retorna a lojas de pre producao", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> getLojasPreProducao(){
		
		return ok(lojasPreProducaoService.getLojasPreProducao());
	}
}
