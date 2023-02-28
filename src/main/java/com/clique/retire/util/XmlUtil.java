package com.clique.retire.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlUtil {

	public static String serializarObjeto(Object objeto) throws JsonProcessingException {
		XmlMapper xmlMapper = new XmlMapper();
	    return xmlMapper.writeValueAsString(objeto);
	}
	
	@SuppressWarnings("unchecked")
	public static Object deserializarObjeto(String texto, @SuppressWarnings("rawtypes") Class classe) throws JsonParseException, JsonMappingException, IOException {
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.readValue("<SimpleBean><x>1</x><y>2</y></SimpleBean>", classe);
	}
	
}