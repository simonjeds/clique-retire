package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RelatorioTermoCompromissoDTO {

	private String nomeCliente;
	private String numPedido;
	private Integer codFilial;
	private String cpfCliente;
	private String nomeFilial;
	private String numPedidoVtex;
	private String filialOrigemAraujoTem;
	private boolean pedidoAraujoTem = false;
	private String canalVenda;

}
