package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Status {

	private Integer numeroPedido;
	private Long numeroPedidoEcommerce;
	private String numeroPedidoEcommerceCliente;
	private String fase;

}