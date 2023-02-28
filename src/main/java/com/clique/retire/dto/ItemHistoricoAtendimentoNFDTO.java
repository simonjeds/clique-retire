package com.clique.retire.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemHistoricoAtendimentoNFDTO {

	private Integer numeroPedido;
	private Integer codigo;
	private String codigoComDigito;
	private String codigoBarras;
	private String descricao;
	private Integer quantidadeRecebida;
	private Integer quantidadePedido;
	private String urlImagem;

	public ItemHistoricoAtendimentoNFDTO(Integer numeroPedido, Integer codigo, String descricao,
			Integer quantidadeRecebida, Integer quantidadePedido, String codigoComDigito) {
		this.numeroPedido = numeroPedido;
		this.codigo = codigo;
		this.descricao = descricao;
		this.quantidadeRecebida = quantidadeRecebida;
		this.quantidadePedido = quantidadePedido;
		this.codigoComDigito = codigoComDigito;
	}
	
	
	public ItemHistoricoAtendimentoNFDTO(Integer numeroPedido, Integer codigo, String descricao,
			String codigoBarras, Integer quantidadeRecebida, String codigoComDigito) {
		this.numeroPedido = numeroPedido;
		this.codigo = codigo;
		this.codigoBarras = codigoBarras;
		this.descricao = descricao;
		this.quantidadeRecebida = quantidadeRecebida;
		this.codigoComDigito = codigoComDigito;
	}
}
