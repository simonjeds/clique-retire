package com.clique.retire.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RelatorioPedidoDevolucaoAraujoTemDTO {

	private Integer numeroPedido;
	private String nomeCliente;
	private String nomeUsuario;
	private String filialOrigem;
	private String filialDestino;
	private Boolean sv;
	private String tipoPagamento;
	
	private List<ProdutoDevolucaoAraujoTemDTO> listProduto;
}
