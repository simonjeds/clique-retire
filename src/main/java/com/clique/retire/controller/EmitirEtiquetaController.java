package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.service.drogatel.EmitirEtiquetaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/emitir-etiqueta", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EmitirEtiquetaController extends BaseController {

	@Autowired
	private EmitirEtiquetaService service;

	@GetMapping
	@ApiOperation("Gera as etiquetas de remedios controlados")
	public ResponseEntity<BaseResponseDTO> gerarEtiqueta(@RequestParam Long numeroPedido) {
		return ok(service.imprimirEtiqueta(numeroPedido));
	}

}
