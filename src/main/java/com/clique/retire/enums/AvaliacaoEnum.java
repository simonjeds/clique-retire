package com.clique.retire.enums;

public enum AvaliacaoEnum {

	QUANTIDADE_SEPARACAO("08", "QUANTIDADE_SEPARACAO"),
	QUANTIDADE_ENTREGA("11", "QUANTIDADE_ENTREGA"),
	QUANTIDADE_APONTAMENTO_FALTA("15", "QUANTIDADE_APONTAMENTO_FALTA"),
	QUANTIDADE_RECEBIMENTO_MERCADORIA("24", "QUANTIDADE_RECEBIMENTO_MERCADORIA");

	AvaliacaoEnum(String codigoFluxo, String descricao) {
		this.codigoFluxo = codigoFluxo;
		this.descricao = descricao;
	}

	private String codigoFluxo;
	private String descricao;
	
	public String getCodigoFluxo() {
		return codigoFluxo;
	}

	public String getDescricao() {
		return descricao;
	}

	public static AvaliacaoEnum buscarPorChave(String codigoFluxo) {
		for (AvaliacaoEnum fase : AvaliacaoEnum.values()) {
			if (fase.getCodigoFluxo().equals(codigoFluxo))
				return fase;
		}
		return null;
	}
	
	public static AvaliacaoEnum buscarPorName(String name) {
		for (AvaliacaoEnum fase : AvaliacaoEnum.values()) {
			if (fase.name().equals(name))
				return fase;
		}
		return null;
	}
}
