package com.clique.retire.enums.converter;

import com.clique.retire.enums.TipoOcorrenciaEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TipoOcorrenciaEnumConverter implements AttributeConverter<TipoOcorrenciaEnum, String> {

	@Override
	public String convertToDatabaseColumn(TipoOcorrenciaEnum tipoOcorrencia) {
		return tipoOcorrencia.getChave();
	}

	@Override
	public TipoOcorrenciaEnum convertToEntityAttribute(String chave) {
		return TipoOcorrenciaEnum.buscarPorChave(chave);
	}

}
