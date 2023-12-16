package com.ks.mspring7.service;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebLoginService extends BaseService{

    public String addressBaseUrl = "http://localhost:8081/";

    public WebLoginService() {
        setWebClient(addressBaseUrl);
    }

    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }

    public Boolean isValidLogin(String username, String userPass, String token) {
        Boolean returnval = false;
    //    localhost:8081/api/v1/auth/authenticate
/*
        // Using WebClient
        Mono<Employee> employeeMono = webClient.post()
                .uri("/api/v1/auth/authenticate")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(employee), Employee.class)
                .retrieve().bodyToMono(AddressResponse.class)
                .block();

         employeeResponse = employeeMono;
*/
        return returnval;
    }

}
