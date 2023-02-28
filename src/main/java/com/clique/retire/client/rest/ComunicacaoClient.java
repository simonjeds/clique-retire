package com.clique.retire.client.rest;

import com.clique.retire.dto.PedidoEditadoEmailDTO;
import feign.RequestLine;

public interface ComunicacaoClient {

    @RequestLine("POST /comunicacao/clique-retire/pedido-alterado")
    void enviarEmailPedidoEditadoCliqueRetire(PedidoEditadoEmailDTO pedido);

}
