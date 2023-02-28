package com.clique.retire.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.clique.retire.enums.SituacaoAtividadeMotociclistaEnum;

@Converter(autoApply = true)
public class SituacaoAtividadeMotociclistaEnumConverter implements AttributeConverter<SituacaoAtividadeMotociclistaEnum, String> {

	@Override
	public String convertToDatabaseColumn(SituacaoAtividadeMotociclistaEnum situacaoEnum) {
		return situacaoEnum.getChave();
	}

	@Override
	public SituacaoAtividadeMotociclistaEnum convertToEntityAttribute(String chave) {
		return SituacaoAtividadeMotociclistaEnum.buscarPorChave(chave);
	}
}