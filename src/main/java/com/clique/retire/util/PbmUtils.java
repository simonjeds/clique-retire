package com.clique.retire.util;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PbmUtils {

	public static boolean isErroPBM(String mensagem) {
		String lowerMessage = StringUtils.defaultString(mensagem).toLowerCase();
		return contemReferenciaErroPBM(lowerMessage);
	}
	
	public static boolean isErroPBMExcetoCartaoRoubacoExtraviado(String mensagem) {
		String lowerMessage = StringUtils.defaultString(mensagem).toLowerCase();
		return !(lowerMessage.contains("cartão roubado") || lowerMessage.contains("cartão extraviado"))
			   && contemReferenciaErroPBM(lowerMessage);
	}
	
	public static boolean isErroPBMCartaoRoubacoExtraviado(String mensagem) {
		String lowerMessage = StringUtils.defaultString(mensagem).toLowerCase();
		return lowerMessage.contains("cartão roubado") || lowerMessage.contains("cartão extraviado");
	}
	
	private static boolean contemReferenciaErroPBM(String mensagem) {
		return mensagem.contains("pbm")
		    	|| mensagem.contains("código retorno tef")
		    	|| mensagem.contains("número autorização já finalizado");
	}

}
