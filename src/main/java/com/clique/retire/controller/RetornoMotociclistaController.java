package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.DadosRetornoMotociclistaDTO;
import com.clique.retire.service.drogatel.RetornoMotociclistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "rest/retorno-motociclista", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class RetornoMotociclistaController extends BaseController {

  private final RetornoMotociclistaService service;

  @GetMapping("consultar-pedido")
  public ResponseEntity<BaseResponseDTO> consultarPedido(@RequestParam String filtro) {
    DadosRetornoMotociclistaDTO pedido = this.service.consultarPedido(filtro);
    return ok(pedido);
  }

  @PostMapping("salvar-retorno")
  public ResponseEntity<BaseResponseDTO> salvarRetorno(@RequestBody DadosRetornoMotociclistaDTO dadosRetorno) throws Exception {
    this.service.salvarRetorno(dadosRetorno);
    return ok(SUCCESS);
  }

}
