package com.clique.retire.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ReceitaSkuDTO;
import com.clique.retire.service.drogatel.CaptacaoPedidoService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rest/captacao", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CaptacaoPedidoController extends BaseController {

  private final CaptacaoPedidoService service;

  @GetMapping("/consulta-captacao")
  @ApiOperation("Consulta pedido para iniciar captação")
  public ResponseEntity<BaseResponseDTO> consultarCaptacao(
    @RequestParam Long numeroPedido, @RequestParam(required = false) boolean refazerCaptacao
  ) {
    return ok(service.consultaCaptacaoPedido(numeroPedido, refazerCaptacao));
  }

  @PostMapping("/registrar-captacao")
  @ApiOperation("Registrar receita para o sku's")
  public ResponseEntity<BaseResponseDTO> salvarCaptacao(@RequestBody ReceitaSkuDTO request) {
    return ok(service.registrarCaptacao(request));
  }

  @GetMapping("/buscar-captacao-realizada")
  public ResponseEntity<BaseResponseDTO> buscarCaptacaoRealizada(@RequestParam Long numeroPedido) {
    return ok(service.buscarDadosCaptacaoRealizada(numeroPedido));
  }
  
  @GetMapping("/permitir-refazer-captacao")
  public boolean permitirRefazerCaptacao(@RequestParam Long numeroPedido) {
    return service.permitirRefazerCaptacao(numeroPedido);
  }

  @PatchMapping("/validar-captacao")
  public ResponseEntity<BaseResponseDTO> validarCaptacao(
    @RequestParam Long numeroPedido, @RequestParam String matriculaConferente
  ) {
    service.validarCaptacao(numeroPedido, matriculaConferente);
    return ok(SUCCESS);
  }

}
