package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties({"codigoProduto", "codigoImagemProduto"})
public class ProdutoFaltaDTO implements UrlImagemDTO {

	private String urlImagem;
	private Integer codigo;
	private String descricao;
	private Integer quantidadePedido;
	private Integer quantidadeFalta;
	private String codigoComDigito;
	private Boolean isControlado;

	private Integer codigoProduto;
	private Integer codigoImagemProduto;

	public ProdutoFaltaDTO(Integer codigo, String descricao, Integer quantidadePedido, Integer digito,
			boolean isControlado) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.quantidadePedido = quantidadePedido;
		this.codigoComDigito = codigo.toString().concat(digito.toString());
		this.isControlado = isControlado;

		this.codigoProduto = codigo;
	}

}
