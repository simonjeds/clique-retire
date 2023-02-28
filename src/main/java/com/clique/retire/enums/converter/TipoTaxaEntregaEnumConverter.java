package com.clique.retire.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.clique.retire.model.enums.TipoTaxaEntregaEnum;

@Converter(autoApply = true)
public class TipoTaxaEntregaEnumConverter implements AttributeConverter<TipoTaxaEntregaEnum, String> {

	@Override
	public String convertToDatabaseColumn(TipoTaxaEntregaEnum tipoEnum) {
		return tipoEnum.getChave();
	}

	@Override
	public TipoTaxaEntregaEnum convertToEntityAttribute(String chave) {
		return TipoTaxaEntregaEnum.buscarPorChave(chave);
	}
}