package com.ks.mspring7.service;

import lombok.Value;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public class BaseService {

    @Autowired
    public WebClient webClient = null;

    protected void setWebClient(String baseAddressUrl) {
        webClient = WebClient.builder().baseUrl(baseAddressUrl)
                .defaultCookie("cookie-name", "cookie-value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
