package com.clique.retire.dto;

import com.clique.retire.enums.TipoPagamentoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModalidadePagamentoDTO {

	private Integer codigo;
	private Double valorPago;
	private Double valorParcela;
	private Integer quantidadeParcelas;
	private boolean possuiJuros;
	private TipoPagamentoEnum tipoPagamento;

}
