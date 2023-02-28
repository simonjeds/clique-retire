package com.clique.retire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.service.drogatel.SeparacaoService;

@RestController
@RequestMapping(value = "/rest/separacao", produces = "application/json;charset=UTF-8")
public class SeparacaoController extends BaseController {

	@Autowired
	private SeparacaoService separacaoService;
	
	@GetMapping("/iniciar-separacao")
	public ResponseEntity<BaseResponseDTO> iniciarSeparacao(@RequestHeader String ip) {
		return ok(separacaoService.iniciarSeparacaoPedidoLoja());
	}
	
	@GetMapping(value="/obter-quantidade-pedidos-aguardando-separacao")
	public ResponseEntity<BaseResponseDTO> obterPedidosAguardandoSeparacao() {
		return ok(separacaoService.obterQuantidadePedidosAguardandoSeparacao());
	}
	
}