package com.clique.retire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaPedidoDrogatelDTO {

	private Integer numeroPedido;
	private Integer produtoMestre;
	private Integer filialProduto;
    private String code;
    private Boolean sucesso;
    private String mensagem;

}