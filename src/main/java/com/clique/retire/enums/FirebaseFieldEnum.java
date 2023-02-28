package com.clique.retire.enums;

import lombok.Getter;

public enum FirebaseFieldEnum {

  NOME_LOJA("nomeLoja"),
  NOVOS_PEDIDOS("novosPedidos"),
  PEDIDOS_PENDENTES("pedidosPendentes"),
  PEDIDOS_CANCELADOS("pedidosCancelados"),
  RANKING_LOJA("rankingLoja"),
  LOJA_DESTAQUE("lojaDestaque"),
  TOTAL_PEDIDOS_LOJA("totalPedidosLoja"),
  TREND_UP("trendUp"),
  TOTAL_PEDIDOS_LOJA_DESTAQUE("totalPedidosLojaDestaque"),
  PEDIDOS_PROCESSADOS_PRAZO("pedidosProcessadosPrazo"),
	PEDIDOS_PROCESSADOS_PRAZO_DESTAQUE("pedidosProcessadosPrazoDestaque"),
  CRONOMETRO_DATA_HORA_INICIO("dataEHoraInicioCronometro"),
  CRONOMETRO_DATA_HORA_FIM("dataEHoraFimCronometro"),
  CRONOMETRO_MENSAGEM("mensagemCronometro"),
  CRONOMETRO_DATA_HORA_FIM_MANUTENCAO("dataEHoraFimManutencao"),
  CRONOMETRO_MENSAGEM_MANUTENCAO("mensagemManutencao"),
  CRONOMETRO_FUNCIONALIDADES("funcionalidades");

	@Getter
	private final String field;
	 
	FirebaseFieldEnum(String field) {
    this.field = field;
	}
}