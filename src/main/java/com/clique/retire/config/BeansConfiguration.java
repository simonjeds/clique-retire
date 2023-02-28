package com.clique.retire.config;

import com.clique.retire.client.interceptor.MsAutorizacaoClientInterceptor;
import com.clique.retire.client.interceptor.SuperVendedorClientInterceptor;
import com.clique.retire.client.rest.MsAutorizacaoClient;
import com.clique.retire.client.rest.ComunicacaoClient;
import com.clique.retire.config.gson.GsonIgnoreStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    public Gson gson() {
        return new GsonBuilder().setExclusionStrategies(new GsonIgnoreStrategy()).create();
    }

    @Bean
    public ComunicacaoClient getComunicacaoClient(SuperVendedorClientInterceptor interceptor) {
        String appBaseUrl = interceptor.getHostNameApp().replaceAll("/$", "");
        String url = appBaseUrl.concat("/sv-comunicacao");
        return Feign.builder()
                .requestInterceptor(interceptor)
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .target(ComunicacaoClient.class, url);
    }

    @Bean
    public MsAutorizacaoClient getAutorizacaoClient(MsAutorizacaoClientInterceptor interceptor) {
        return Feign.builder()
                .requestInterceptor(interceptor)
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .target(MsAutorizacaoClient.class, interceptor.getServiceUrl());
    }

}
