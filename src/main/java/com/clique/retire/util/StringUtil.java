package com.clique.retire.util;

import java.text.Normalizer;

public class StringUtil {
	
	private StringUtil() {}
	
	public static String removerAcentos(String str) {
	    return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
}
