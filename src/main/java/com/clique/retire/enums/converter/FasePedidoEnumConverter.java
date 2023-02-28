package com.clique.retire.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.clique.retire.enums.FasePedidoEnum;

@Converter(autoApply = true)
public class FasePedidoEnumConverter implements AttributeConverter<FasePedidoEnum, String> {

	@Override
	public String convertToDatabaseColumn(FasePedidoEnum faseEnum) {
		return faseEnum.getChave();
	}

	@Override
	public FasePedidoEnum convertToEntityAttribute(String chave) {
		return FasePedidoEnum.buscarPorChave(chave);
	}
}
