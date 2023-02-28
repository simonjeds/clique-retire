package com.clique.retire.client.interceptor;

import com.clique.retire.service.drogatel.GeraToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SuperVendedorClientInterceptor extends GeraToken implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + getAuthToken());
        requestTemplate.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

}
