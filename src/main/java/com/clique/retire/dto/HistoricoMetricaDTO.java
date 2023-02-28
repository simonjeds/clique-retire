package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoMetricaDTO {

	private Integer id;
	private Integer numeroPedido;
	private Integer numeroFilial;
	private String nomeFilial;
	private Integer tempoIntegracao;
	private Integer tempoInicioSeparacao;
	private Integer tempoRegistro;
	private Integer novosPedidos = 0;
	private Integer quantidadePedidosDestaLoja = 0;
	
}