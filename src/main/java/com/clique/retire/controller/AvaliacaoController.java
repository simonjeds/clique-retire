package com.clique.retire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.service.drogatel.AvaliacaoService;
import com.clique.retire.util.WebUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/rest/avaliacao", produces = "application/json;charset=UTF-8")
public class AvaliacaoController extends BaseController {

	private static final String AVALIACAO_REALIZADA_COM_SUCESSO = "Avaliação realizada com sucesso.";
	
	@Autowired
	private AvaliacaoService avaliacaoService;

	@GetMapping(value = "/realizar-avaliacao")
	@ApiOperation(value = "Realiza a avaliação", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> salvarAvaliacao(@RequestParam("nota") Integer nota,
			@RequestParam("comentario") String comentario, @RequestHeader(value = "IP", required = true) String ip){
			Integer codigoUsuario = getCodigoUsuarioLogado();
			avaliacaoService.salvarAvaliacao(WebUtils.getClientIp(), codigoUsuario, comentario, nota);
		return ok(AVALIACAO_REALIZADA_COM_SUCESSO);
	}
	
	@GetMapping(value = "/validar-avaliacao")
	public ResponseEntity<BaseResponseDTO> validarAvaliacao(){
		Integer codigoUsuario = getCodigoUsuarioLogado();
		avaliacaoService.validarAvaliacao(codigoUsuario);

		return ok(AVALIACAO_REALIZADA_COM_SUCESSO);
	}

}
