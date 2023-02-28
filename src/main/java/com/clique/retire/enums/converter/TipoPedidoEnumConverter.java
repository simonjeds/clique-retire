package com.clique.retire.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.clique.retire.enums.TipoPedidoEnum;

@Converter(autoApply = true)
public class TipoPedidoEnumConverter implements AttributeConverter<TipoPedidoEnum, String> {

	@Override
	public String convertToDatabaseColumn(TipoPedidoEnum tipoEnum) {
		return tipoEnum.getChave();
	}

	@Override
	public TipoPedidoEnum convertToEntityAttribute(String chave) {
		return TipoPedidoEnum.buscarPorChave(chave);
	}
}