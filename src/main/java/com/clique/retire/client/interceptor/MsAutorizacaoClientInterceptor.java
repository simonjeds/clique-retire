package com.clique.retire.client.interceptor;

import com.clique.retire.config.properties.MsAutorizacaoConfigurationProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MsAutorizacaoClientInterceptor implements RequestInterceptor {

    private static final String CLIENT_ID = "client_id";
    private static final String ACCESS_TOKEN = "access_token";

    private final MsAutorizacaoConfigurationProperties configuration;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        requestTemplate.header(CLIENT_ID, configuration.getClientId());
        requestTemplate.header(ACCESS_TOKEN, configuration.getAccessToken());
    }

    public String getServiceUrl() {
        return configuration.getUrl();
    }

}
