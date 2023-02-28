package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProdutoDevolucaoAraujoTemDTO {

	private String codigoBarra;
	private String codigoProduto;
	private String nomeProduto;
	private Integer quantidade;
	private Integer emFalta;
	private Integer codigoProdutoSemDigito;
}
