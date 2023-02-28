package com.clique.retire.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.PanelDTO;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.service.drogatel.PanelService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;



@RestController
@RequestMapping(value = "/rest/panel", produces = "application/json;charset=UTF-8")
public class PanelController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PanelController.class);
	
	@Autowired
	private PanelService panelService;
	
	@GetMapping(value="/consulta-panel-operacional")
	@ApiOperation(value = "Obtem os dados para opainel operacional", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<List<PanelDTO>> getPanelList(@RequestParam("nomeLoja") String polo) {
		try {
			LOGGER.info("[Pesquisar dados para o painel] ");
			return ResponseEntity.ok(panelService.getPanelList(polo));
		}catch (BusinessException e) {
			LOGGER.error("[Erro na pesquisa] ");
			return null;
		}
	}
}
