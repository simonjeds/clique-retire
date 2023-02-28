package com.clique.retire.client.rest.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collections;
import java.util.Objects;

public class AbstractRestClient {

  private final String baseUrl;
  private RestTemplate restTemplate;

  public AbstractRestClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  protected RestTemplate getRestTemplate() {
    if (Objects.isNull(restTemplate)) {
      DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory(this.baseUrl);

      restTemplate = new RestTemplate();
      restTemplate.setUriTemplateHandler(uriBuilderFactory);
    }

    return restTemplate;
  }

  protected HttpHeaders getHttpHeaders() {
    return this.getHttpHeaders(null);
  }

  protected HttpHeaders getHttpHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

    if (StringUtils.isNotBlank(token)) {
      headers.setBearerAuth(token);
    }

    return headers;
  }

}
