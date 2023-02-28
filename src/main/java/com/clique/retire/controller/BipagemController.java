package com.clique.retire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ItensProdutoDTO;
import com.clique.retire.service.drogatel.BipagemLoteService;
import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rest/pedidos", produces = "application/json;charset=UTF-8")
public class BipagemController extends BaseController {

	@Autowired
	private BipagemLoteService service;

	@PostMapping("/{id}/bipagem/itens")
	@ApiOperation(value = "Bipagem de lotes por medicamento")
	public ResponseEntity<BaseResponseDTO> separarItem(
			@ApiParam(value = "id") @PathVariable(value = "id", required = true) Long id,
			@ApiParam(value = "itemPedido", required = true) @RequestBody ItensProdutoDTO item) {

		log.info("[BIPAGEM] Bipagem de lote - pedido: {}", new Gson().toJson(item));
		return ok(service.separarItem(id, item));
	}

}