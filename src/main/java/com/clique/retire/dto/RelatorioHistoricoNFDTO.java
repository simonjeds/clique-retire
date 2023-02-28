package com.clique.retire.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RelatorioHistoricoNFDTO {

	private String chave;
	private String recebedor;
	private Date dataRecebimento;
	private Integer numeroPedido;
	private String cliente;
	private String filial;
	private boolean pedidoConcluido;
	private boolean araujoTem = false;
	private List<ItemHistoricoAtendimentoNFDTO> itens = new ArrayList<>();

	public RelatorioHistoricoNFDTO(String chave, String recebedor, Date dataRecebimento, Integer numeroPedido,
			String cliente, boolean pedidoConcluido, String filial, Integer filialAraujoTem) {
		super();
		this.chave = chave;
		this.recebedor = recebedor;
		this.dataRecebimento = dataRecebimento;
		this.numeroPedido = numeroPedido;
		this.cliente = cliente;
		this.pedidoConcluido = pedidoConcluido;
		this.filial = filial;
	}
}
