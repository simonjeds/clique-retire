package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.SolicitacaoCancelamentoPedidoDTO;
import com.clique.retire.service.drogatel.CancelamentoPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "rest/cancelamento-pedido", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CancelamentoPedidoController extends BaseController {

    @Autowired
    private CancelamentoPedidoService service;

    @GetMapping("obter-pedido-cancelado")
    public ResponseEntity<BaseResponseDTO> obterPedidoCancelado(@RequestHeader(value = "IP", required = true) String ip) {
        return ok(service.buscarPedidoCancelamentoPorUsuarioOuFilial());
    }

    @PutMapping("finalizar-retorno-mercadoria")
    public ResponseEntity<BaseResponseDTO> finalizarRetornoMercadoria(@RequestHeader(value = "IP", required = true) String ip) {
        return ok(service.finalizarRetornoMercadoria());
    }

    @PostMapping("estorno")
    public ResponseEntity<BaseResponseDTO> estornarPedido(@RequestBody SolicitacaoCancelamentoPedidoDTO request) {
        return ok(service.cancelarPedidoControlado(request));
    }

}