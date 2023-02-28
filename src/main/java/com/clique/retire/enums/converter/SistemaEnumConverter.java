package com.clique.retire.enums.converter;

import com.clique.retire.enums.SistemaEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SistemaEnumConverter implements AttributeConverter<SistemaEnum, String> {

	@Override
	public String convertToDatabaseColumn(SistemaEnum sistema) {
		return sistema.getChave();
	}

	@Override
	public SistemaEnum convertToEntityAttribute(String chave) {
		return SistemaEnum.buscarPorChave(chave);
	}

}
