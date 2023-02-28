package com.clique.retire.dto;

import lombok.Data;

@Data
public class PedidoSeparacaoDTO {

	private boolean pedidoAndamento;
	private boolean controlado;
	private boolean semPedido;
	private Long numeroPedido;
	private boolean pedidoAraujoTem;
	private Integer codigoFilialGerencial;
	private boolean pedidoSuperVendedor;
	private boolean editado;
	private boolean fracionado;
}
