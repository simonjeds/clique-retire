package com.clique.retire.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoProblemaEnum {

	ESTOQUE_DIVERGENTE("E"),
	PRODUTO_RESERVADO("R"),
	CONSUMO_IMPROMPRIO("I");

	private String chave;

	public static TipoProblemaEnum buscarPorChave(String chave) {
		return Arrays.asList(TipoProblemaEnum.values()).stream()
				.filter(p -> p.getChave().equalsIgnoreCase(chave))
				.findFirst()
				.orElse(null);
	}
}