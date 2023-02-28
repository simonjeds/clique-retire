package com.clique.retire.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoConselhoEnum {

	CRM("M","CRM"),
	CRMV("V","CRMV"),
	CRO("O","CRO"),
	COREN("E","COREN");
	
	private String chave;
	private String  nome;

	public static TipoConselhoEnum buscarPorName(String name) {
		return Arrays.stream(TipoConselhoEnum.values())
			.filter(conselho -> conselho.name().equalsIgnoreCase(name))
			.findAny()
			.orElse(null);
	}
	
	public static TipoConselhoEnum buscarPorChave(String chave) {
		return Arrays.stream(TipoConselhoEnum.values())
			.filter(conselho -> conselho.chave.equalsIgnoreCase(chave))
			.findAny()
			.orElse(null);
	}
}
