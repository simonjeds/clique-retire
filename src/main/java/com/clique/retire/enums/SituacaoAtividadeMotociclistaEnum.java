package com.clique.retire.enums;

public enum SituacaoAtividadeMotociclistaEnum {

	LIVRE("1", "Livre"),
	OCUPADO("2", "Ocupado"),
	INDISPONIVEL("3", "Indisponível"),
	TRANSFERIDO("4", "Transferido"),
	PONTO_FECHADO("5", "Ponto Fechado"),
	PENDENTE("6", "Pendente"),
	AUTORIZADO("7", "Autorizado"),
	NEGADO("8", "Negado");
	
	SituacaoAtividadeMotociclistaEnum(String chave, String valor) {
		this.chave = chave;
		this.valor = valor;
	}

	private String chave;
	private String valor;

	public String getChave() {
		return chave;
	}

	public String getValor() {
		return valor;
	}

	public static SituacaoAtividadeMotociclistaEnum buscarPorChave(String chave) {
		for (SituacaoAtividadeMotociclistaEnum e : SituacaoAtividadeMotociclistaEnum.values()) {
			if (e.getChave().equalsIgnoreCase(chave))
				return e;
		}
		return null;
	}
}