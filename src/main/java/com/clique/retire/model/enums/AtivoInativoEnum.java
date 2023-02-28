package com.clique.retire.model.enums;

import lombok.Getter;

public enum AtivoInativoEnum {

	A("A"),
	I("I");

	@Getter
	private String descricao;

	private AtivoInativoEnum(String descricao) {
		this.descricao = descricao;
	}
}