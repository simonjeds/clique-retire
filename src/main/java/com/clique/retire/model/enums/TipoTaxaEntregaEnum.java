package com.clique.retire.model.enums;

public enum TipoTaxaEntregaEnum {
	NAO_PAGO("NPG", "Não pago"),
	BOLETO_BANCARIO("BOL","Boleto Bancário"),
	CARTAO_CREDITO("CAC", "Cartão de Crédito"),
	CARTAO_DEBITO("CAD","Cartão Débito"),
	CHEQUE("CHQ","Cheque"),
	CONVENIO("CON","Convênio"),
	DEPOSITO_BANCARIO("DEP","Depósito Bancário"),
	DINHEIRO("DIN", "Dinheiro"),
	PAGAMENTO_ANTECIPADO("PAN","Pagamento Antecipado"),
	VALE("VAL","Vale");

	TipoTaxaEntregaEnum(String chave, String valor) {
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

	public static TipoTaxaEntregaEnum buscarPorChave(String chave) {
		for (TipoTaxaEntregaEnum e : TipoTaxaEntregaEnum.values()) {
			if (e.getChave().equals(chave))
				return e;
		}
		return null;
	}
}
