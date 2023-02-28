package com.clique.retire.client.rest;

import com.clique.retire.dto.CancelamentoPedidoDrogatelDTO;
import com.clique.retire.dto.MovimentoPedidoDrogatelDTO;
import com.clique.retire.dto.RespostaPedidoDrogatelDTO;

import feign.Headers;
import feign.RequestLine;
import feign.Response;

public interface DrogatelClient {

    @RequestLine("POST")
    @Headers({ "Content-Type: application/json" })
    RespostaPedidoDrogatelDTO cancelarPedido(CancelamentoPedidoDrogatelDTO pedido);
    
    @RequestLine("POST")
    @Headers({ "Content-Type: application/json" })
    RespostaPedidoDrogatelDTO movimentacaoPedidoEcommerce(MovimentoPedidoDrogatelDTO pedido);
    
    @RequestLine("POST")
    @Headers({ "Content-Type: application/json" })
    Response criarPedidoDeServico();
    
    @RequestLine("POST")
    @Headers({ "Content-Type: application/json" })
    RespostaPedidoDrogatelDTO sinalizarApontamentoFaltaZeroBalcao();

}
