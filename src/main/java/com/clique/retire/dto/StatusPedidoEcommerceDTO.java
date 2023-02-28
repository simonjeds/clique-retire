package com.clique.retire.dto;

import java.util.Date;

import lombok.Data;

@Data
public class StatusPedidoEcommerceDTO {

	private Integer numeroPedido;
	private Long numeroPedidoEcommerce;
	private String fase;
	private String numeroPedidoEcommerceCliente;
	private Date dataAtualizacao;
	private Double valorNotaFiscal;
	private Integer codigoFilial;
	private String nomeCliente;
	private String complemento;

}