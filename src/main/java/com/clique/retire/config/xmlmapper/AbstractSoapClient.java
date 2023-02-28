package com.clique.retire.config.xmlmapper;

import com.clique.retire.infra.exception.SoapClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Objects;

public abstract class AbstractSoapClient {

    private static final String NAMESPACE_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String XML_TEMPLATE =
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"{NAMESPACE}\">" +
            "<soap:Body>" +
                "{BODY}" +
            "</soap:Body>" +
        "</soap:Envelope>";

    private final String namespace;
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final String requestTemplate;

    protected AbstractSoapClient(RestTemplate restTemplate, String namespace) {
        this.namespace = namespace;
        this.restTemplate = restTemplate;
        this.requestTemplate = XML_TEMPLATE.replace("{NAMESPACE}", namespace);
        this.xmlMapper = new XmlMapperDecorator(new XmlMapper(), "web");
        this.xmlMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ResponseEntity<String> callWs(String url, Object body) {
        try {
            String requestBody = requestTemplate.replace("{BODY}", xmlMapper.writeValueAsString(body));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.TEXT_XML);
            httpHeaders.setContentLength(requestBody.length());
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

            return this.restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        } catch (JsonProcessingException exception) {
            throw new SoapClientException(exception);
        }
    }

    public <T> T extractResponse(ResponseEntity<String> response, String responseWrapperTag, Class<T> resultType) {
        String body = response.getBody();
        if (Objects.isNull(body)) {
            return null;
        }

        Element soapBody = this.extractBodyElement(body);
        Element value = soapBody.getChild(responseWrapperTag, Namespace.getNamespace(namespace));
        XMLOutputter xmlOutputter = new XMLOutputter();
        String resultString = xmlOutputter.outputString(value);
        try {
            return this.xmlMapper.readValue(resultString, resultType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Element extractBodyElement(String body) {
        try {
            StringReader reader = new StringReader(body);
            SAXBuilder saxBuilder = new SAXBuilder();
            saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            saxBuilder.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Document document = saxBuilder.build(reader);
            Element soapEnvelope = document.getRootElement();
            return soapEnvelope.getChild("Body", Namespace.getNamespace(NAMESPACE_SOAP));
        } catch (JDOMException | IOException exception) {
            throw new SoapClientException(exception);
        }
    }

}
