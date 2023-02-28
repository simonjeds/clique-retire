package com.clique.retire.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistroPedidoDTO {

	private boolean receitaDigital;
	private boolean pagamentoEmDinheiro;
	private boolean nfEmitida;

}
