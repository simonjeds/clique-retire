package com.clique.retire.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class PrePedidoDTO {

	private Integer codigoOrigem;
	private Integer codigoLoja;
	private String tipoPrePedido;
	private Integer codigoCliente;

}