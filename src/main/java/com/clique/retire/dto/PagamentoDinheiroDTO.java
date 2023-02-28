package com.clique.retire.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagamentoDinheiroDTO {

	private Integer codigoModalidadePagamento;
	private Double valor;
	private Double troco;
}
