package com.clique.retire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagamentoComunicacaoDTO {

    private String tipoPagamento;
    private String tipoCartao;
    private String digitosFinaisCartao;
    private Integer quantidadeParcelas;
    private Double valorTotalPedido;
    private Double valorTotalItensPedido;
    private Double valorFrete;
    private Double valorJuros;
    private Double valorDesconto;

}