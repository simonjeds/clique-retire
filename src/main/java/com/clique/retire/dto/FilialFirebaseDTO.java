package com.clique.retire.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class FilialFirebaseDTO {

	private Integer rankingLoja;
	private Integer totalPedidosLoja;
	private String pedidosProcessadosPrazo;
	
	private Integer trendUp;
	
	private String nomeLoja;
	private Integer numeroFilial;
	private Integer novosPedidos;
	private Integer tempoProcessamento;
	private Double percentualPedidosProcessados;
	
	private String lojaDestaque;
	private Integer totalPedidosLojaDestaque;
	private String pedidosProcessadosPrazoDestaque;

	public void setRankingLoja(Object rankingLoja) {
		this.rankingLoja = StringUtils.isNumeric(String.valueOf(rankingLoja)) ? Integer.parseInt(String.valueOf(rankingLoja)) : null;
	}

	public void setTotalPedidosLoja(Object totalPedidosLoja) {
		this.totalPedidosLoja = StringUtils.isNumeric(String.valueOf(totalPedidosLoja)) ? Integer.parseInt(String.valueOf(totalPedidosLoja)) : null;
	}

	public void setTotalPedidosLojaDestaque(Object totalPedidosLojaDestaque) {
		this.totalPedidosLojaDestaque = StringUtils.isNumeric(String.valueOf(totalPedidosLojaDestaque)) ? Integer.parseInt(String.valueOf(totalPedidosLojaDestaque)) : null;
	}
}
