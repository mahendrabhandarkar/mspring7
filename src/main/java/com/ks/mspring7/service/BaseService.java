package com.ks.mspring7.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BaseService {

    public WebClient webClient = null;

    protected void setWebClient(String baseAddressUrl) {
        webClient = WebClient.builder().baseUrl(baseAddressUrl)
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.customCodecs()
                        .register(new Jackson2JsonDecoder(new ObjectMapper(), new MimeType("text","string")))
                )
                .defaultCookie("cookie-name", "cookie-value")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
           /*     .filter(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
             */   .build();
    }
/*
    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if(log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                // append clientRequest method and url
                clientRequest.headers()
                        .forEach((name, values) -> values.forEach(value ->

                        ));
            }
            return Mono.just(clientRequest);
        });
    }
    */
}
