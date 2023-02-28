package com.clique.retire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ClienteDTO;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.service.drogatel.ClienteService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rest/consulta-por-cpf", produces = "application/json;charset=UTF-8")
public class ClienteController extends BaseController {

	@Autowired
	private ClienteService service;

	@GetMapping
	@ApiOperation(value = "Consulta cliente por documento", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> consultarDocumento(
			@ApiParam("documento") @RequestParam("documento") String documento) {

		try {
			return ok(service.consultarDocumento(documento));
		} catch (BusinessException e) {
			return erro(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return erro(OCORREU_UM_ERRO);
		}
	}

	@ApiOperation(value = "validação dos dados do cliente", notes = "validação dos dados do cliente")
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<BaseResponseDTO> validar(
			@ApiParam(value = "cliente", required = true) @RequestBody ClienteDTO cliente) {

		try {
			service.validacaoCliente(cliente);
			return ok(SUCCESS);
		} catch (BusinessException e) {
			return erro(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return erro(OCORREU_UM_ERRO);
		}

	}

}
