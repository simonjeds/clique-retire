package com.clique.retire.dto;

import com.clique.retire.util.NumberUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ItemPedidoSIACDTO {

	private Integer codigoItemPedido;
	private Integer codigoProduto;
	private Integer quantidadeProduto;
	private Integer numeroPedido;
	private Double precoAnterior;
	private Double novoPreco;
	private Integer codigoFilial;
	
	public double getDiferencaPreco() {
		double diferenca = precoAnterior.doubleValue() - novoPreco.doubleValue();
		return NumberUtil.round(diferenca * quantidadeProduto.intValue(),2);
	}
}
