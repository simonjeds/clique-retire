package com.clique.retire.config.xmlmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

public class XmlMapperDecorator extends XmlMapper {

    private final XmlMapper xmlMapper;
    private final String namespacePrefix;

    public XmlMapperDecorator(XmlMapper xmlMapper, String namespacePrefix) {
        this.xmlMapper = xmlMapper;
        this.namespacePrefix = namespacePrefix;
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        final Class<?> clazz = value.getClass();
        final String jsonValue = clazz.isAssignableFrom(String.class)
                ? value.toString()
                : this.xmlMapper.writeValueAsString(value);
        if (clazz.isAnnotationPresent(HideXmlRootElement.class)) {
            return removeWrapperTag(jsonValue);
        }
        return addNamespacePrefix(jsonValue, namespacePrefix);
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws IOException {
        String normalizedContent = removeNamespacePrefixes(content);
        return super.readValue(normalizedContent, valueType);
    }

    private String removeWrapperTag(String value) {
        return value.trim()
                .replaceAll("^<[^>]+>", "")
                .replaceAll("</[^<]+>$", "");
    }

    private String addNamespacePrefix(String value, String namespace) {
        return value.trim()
                .replaceAll("^<([^>]+)>", String.format("<%s:$1>", namespace))
                .replaceAll("</([^<]+)>$", String.format("</%s:$1>", namespace));
    }

    private String removeNamespacePrefixes(String value) {
        return value.trim()
                .replaceAll("\\s.+=\".+\"", "")
                .replaceAll("<[^/|<]+:([^>]+>)", "<$1")
                .replaceAll("</[^>]+:([^>]+>)", "</$1");
    }

}
