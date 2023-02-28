package com.clique.retire.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RelatorioEnum {

	PEDIDO_SEPARACAO("classpath:templates/pedido/pedidoSeparacao.jrxml", "classpath:templates/pedido/pedidoSeparacao.pdf"),
	PEDIDO_SEPARACAO_CONTROLADO("classpath:templates/pedido/pedidoSeparacaoControlado.jrxml", "classpath:templates/pedido/pedidoSeparacaoControlado.pdf"),
	PEDIDO_SEPARACAO_ARAUJO_TEM("classpath:templates/pedido/pedidoSeparacaoAraujoTem.jrxml", "classpath:templates/pedido/pedidoSeparacaoAraujoTem.pdf"),
	TERMO_COMPROMISSO("classpath:templates/termoCompromisso/termoCompromisso.jrxml", "classpath:templates/pedido/termoCompromisso.pdf"),
	HISTORICO_NF("classpath:templates/nfPedido/nfPedido.jrxml", "classpath:templates/nfPedido/nfPedido.pdf"),
	PEDIDO_SEPARACAO_COMANDA("classpath:templates/pedido/pedidoSeparacaoNovaComanda.jrxml", "classpath:templates/pedido/pedidoSeparacaoNovaComanda.pdf"),
	PEDIDO_SEPARACAO_COMANDA_CANHOTO("classpath:templates/pedido/pedidoSeparacaoCanhoto.jrxml", "classpath:templates/pedido/pedidoSeparacaoCanhoto.pdf"),
	PEDIDO_DEVOLUCAO_ARAUJO_TEM("classpath:templates/pedido/pedidoDevolucaoAraujoTem.jrxml", "classpath:templates/pedido/pedidoDevolucaoAraujoTem.pdf"),
	PEDIDO_SEPARACAO_EDITADO("classpath:templates/pedido/pedidoSeparacaoEditado.jrxml", "classpath:templates/pedido/pedidoSeparacaoEditado.pdf"),
	PEDIDO_SEPARACAO_CONTROLADO_EDITADO("classpath:templates/pedido/pedidoSeparacaoControladoEditado.jrxml", "classpath:templates/pedido/pedidoSeparacaoControladoEditado.pdf"),

	PEDIDO_FRACIONADO("classpath:templates/pedido/fracionamento.jrxml", "classpath:templates/pedido/fracionamento.pdf");

	@Getter
	private String jrxmlPath;

	@Getter
	private String destinationPath;

}