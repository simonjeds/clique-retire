package com.clique.retire.enums;

public enum TipoPedidoEnum {

	TRANSFERENCIA("T", "TRANSFERÊNCIA"),
	ARAUJOTEM("A", "ARAUJO TEM"),
	DROGATEL("D", "DROGATEL"),
	MERCADORIA("M", "MERCADORIA"),
	SERVICO("S", "SERVIÇO"),
	LOTE("L", "LOTE"),
	PESSOA_JURIDICA("P", "PESSOA JURÍDICA"),
	ARAUJO_EXPRESS("X", "ARAUJO EXPRESS"),
	E_COMMERCE("E", "SITE"),
	TELEMARKETING_ATIVO("K", "TELEMARKETING ATIVO"),
	CASSI("C", "CASSI"),
	APLICATIVO("F", "APLICATIVO DROGARIA ARAUJO"),
	IFOOD("I", "IFOOD"),
	WHATSUP("W", "WHATSUP"),
	ATACADO("O", "ATACADO");

	
	TipoPedidoEnum(String chave, String valor) {
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

	public static TipoPedidoEnum buscarPorChave(String chave) {
		for (TipoPedidoEnum e : TipoPedidoEnum.values()) {
			if (e.getChave().equalsIgnoreCase(chave))
				return e;
		}
		return null;
	}
}