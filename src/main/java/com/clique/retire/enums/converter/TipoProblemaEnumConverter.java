package com.clique.retire.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.clique.retire.enums.TipoProblemaEnum;

@Converter(autoApply = true)
public class TipoProblemaEnumConverter implements AttributeConverter<TipoProblemaEnum, String> {

	@Override
	public String convertToDatabaseColumn(TipoProblemaEnum tipoEnum) {
		return tipoEnum.getChave();
	}

	@Override
	public TipoProblemaEnum convertToEntityAttribute(String chave) {
		return TipoProblemaEnum.buscarPorChave(chave);
	}
}