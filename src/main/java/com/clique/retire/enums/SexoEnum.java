package com.clique.retire.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SexoEnum {

	M("Masculino"),
	F("Feminino"),
	O("Outro");
	
	private final String descricao;

	public static SexoEnum getValorPorSigla(String sigla) {
		return Arrays.stream(SexoEnum.values())
			.filter(value -> value.name().equalsIgnoreCase(sigla))
			.findFirst()
			.orElse(null);
	}

}