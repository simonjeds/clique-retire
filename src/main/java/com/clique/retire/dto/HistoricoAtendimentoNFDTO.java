package com.clique.retire.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HistoricoAtendimentoNFDTO {

	private Integer numeroPedido;
	private String cliente;
	private boolean pedidoConcluido;
	private List<ItemHistoricoAtendimentoNFDTO> itens = new ArrayList<>();

	public HistoricoAtendimentoNFDTO(Integer numeroPedido, String cliente, boolean pedidoConcluido) {
		this.numeroPedido = numeroPedido;
		this.cliente = cliente;
		this.pedidoConcluido = pedidoConcluido;
	}
}