package com.clique.retire.client.rest;

import com.clique.retire.dto.OrigemPrePedidoEcommerceResponseDTO;
import com.clique.retire.dto.PrePedidoDTO;

import feign.RequestLine;

public interface IntegradorSiacClient {

  @RequestLine("POST /associaOrigemPreVenda")
  OrigemPrePedidoEcommerceResponseDTO gerarPrePedido(PrePedidoDTO prePedidoDTO);

}
