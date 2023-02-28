package com.clique.retire.enums;

public enum FasePedidoEnum {

	DESISTENCIA("01", FasePedidoEnum.TEXTO_DESISTENCIA, FasePedidoEnum.TEXTO_DESISTENCIA, ""),
	ATENDIDO("03", "Atendido", "Aguardando separação", "Ag. Separação"),
	EM_ANALISE_RISCO("04", FasePedidoEnum.ANALISE_RISCO, FasePedidoEnum.ANALISE_RISCO, FasePedidoEnum.ANALISE_RISCO),
	AGUARDANDO_ATENDIMENTO("05", "Aguardando atendimento", "Aguardando atendimento", ""),
	EM_SEPARACAO("06", "Em Separação", "Em Separação", ""),
	EM_NEGOCIACAO("07", "Em Negociação", "Aguardando negociação", FasePedidoEnum.AGENCIA_DROGATEL),
	EM_REGISTRO("08", "Em Registro", "Em registro", "Ag. Emitir Cupom"),
	NAO_ENTREGUE("09", FasePedidoEnum.TEXTO_NAO_ENTREGUE, FasePedidoEnum.TEXTO_NAO_ENTREGUE, FasePedidoEnum.TEXTO_NAO_ENTREGUE),
	EXPEDIDO("10", FasePedidoEnum.TEXTO_EXPEDIDO, FasePedidoEnum.TEXTO_EXPEDIDO, FasePedidoEnum.TEXTO_EXPEDIDO),
	ENTREGUE("11", "Entregue", "Entregue", "Já retirado"),
	CANCELADO("12", FasePedidoEnum.TEXTO_CANCELADO, FasePedidoEnum.TEXTO_CANCELADO, FasePedidoEnum.TEXTO_CANCELADO),
	DEVOLUCAO_TOTAL("13", "Devolução Total", "Devolvido", FasePedidoEnum.TEXTO_CANCELADO),
	AGUARDANDO_NEGOCIACAO("15", "Ag. Negociação", "Aguardando negociação", FasePedidoEnum.AGENCIA_DROGATEL),
	AGUARDANDO_NEGOCIACAO_REINCIDENTE("16", "Ag. Negociação Reincidente", "Aguardando negociação reincidente", FasePedidoEnum.AGENCIA_DROGATEL),
	AGUARDANDO_CONFIRMACAO_PAGAMENTO("27", "Ag. Conf. Pagamento", "Aguardando confirmação de pagamento", "Ag. Conf. Pagamento"),
	AGUARDANDO_REGISTRO("18", "Ag. Registro", "Aguardando registro", ""),
	AGUARDANDO_EXPEDICAO("19", "Ag. Expedição", "Aguardando retirada", "Pronto para retirada"),
	AGUARDANDO_MERCADORIA("20", "Ag. Mercadoria", "Aguardando mercadoria", ""),
	EMITIDO("21", "Emitido", "Emitido", ""),
	AGUARDANDO_EXPEDICAO_ROTEIRIZADA("22", "Aguardando Expedicao Roteirizada", "Aguardando Expedicao Roteirizada", ""),
	ENVIADO("23", "Enviado", "Enviado", ""),
	RECEBIDO("24", "Recebido", "Recebido", ""),
	RECEBIDO_COM_PENDENCIA("25", "Recebido com pendencia", "Recebido com pendencia", ""),
	ENTREGUE_COM_PENDENCIA("26", "Entregue com Pendência", "Entregue c/ Pendência", ""),
	AGUARDANDO_APROVACAO_RECEITA("28", "Ag. aprovação de Receita", "Aguardando receitas e lotes", ""),
	AGUARDANDO_RECEITA("29", "Ag. aprovação de Receita", "Aguardando receita", "Aguardando receita"),
	AGUARDANDO_PAGAMENTO_GNRE("30", "Aguardando Pagamento GNRE", "Aguardando Pagamento GNRE", ""),
	AGUARDANDO_PRAZO_INICIO_SEPARACAO("31", "Aguardando prazo para inicio de separação", "Aguardando prazo para inicio de separação", ""),
	SOLICITANDO_CAPTURA("33", FasePedidoEnum.TEXTO_SOLICITANDO_CAPTURA, FasePedidoEnum.TEXTO_SOLICITANDO_CAPTURA, ""),
	AGUARDANDO_RECEITAS("34", FasePedidoEnum.TEXTO_EM_EDICAO, FasePedidoEnum.TEXTO_EM_EDICAO, FasePedidoEnum.TEXTO_EM_EDICAO);
	
	FasePedidoEnum(String chave, String valor, String descricaoPainel, String descricaoCombo) {
		this.chave = chave;
		this.valor = valor;
		this.descricaoPainel = descricaoPainel;
		this.descricaoCombo = descricaoCombo;
	}

	private String chave;
	private String valor;
	private String descricaoPainel;
	private String descricaoCombo;

	public static final String TEXTO_DESISTENCIA = "Desistência";
	public static final String ANALISE_RISCO = "Em Análise de Risco";
	public static final String AGENCIA_DROGATEL = "Ag. Drogatel";
	public static final String TEXTO_NAO_ENTREGUE = "Não Entregue";
	public static final String TEXTO_EXPEDIDO = "Expedido";
	public static final String TEXTO_CANCELADO = "Cancelado";
	public static final String TEXTO_SOLICITANDO_CAPTURA = "Solicitando captura";
	public static final String TEXTO_EM_EDICAO = "Em edição";

	public String getChave() {
		return chave;
	}

	public String getValor() {
		return valor;
	}

	public String getDescricaoPainel() {
		return descricaoPainel;
	}

	public static FasePedidoEnum buscarPorChave(String chave) {
		for (FasePedidoEnum fase : FasePedidoEnum.values()) {
			if (fase.getChave().equals(chave))
				return fase;
		}
		return null;
	}
	
	public static FasePedidoEnum buscarPorName(String name) {
		for (FasePedidoEnum fase : FasePedidoEnum.values()) {
			if (fase.name().equals(name))
				return fase;
		}
		return null;
	}

	public String getDescricaoCombo() {
		return descricaoCombo;
	}

}
