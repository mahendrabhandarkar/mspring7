package com.ks.mspring7.service;

import com.google.gson.Gson;
import com.ks.mspring7.proxy.GenericRestClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class WebLoginService extends BaseService{

    public String addressBaseUrl = "http://localhost:8081";

    public WebLoginService() {
        setWebClient(addressBaseUrl);
    }

    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }

    @Autowired
    private GenericRestClient genericClient;

    public Object isValidLogin(String username, String userPass, String token) {
        Boolean returnval = false;

        // api/v1/auth/register -- This is used for register
        HashMap<String, String> requestBody1 = new HashMap<>();
        requestBody1.put("firstname", "ADMIN");
        requestBody1.put("lastname", "Admin");
        requestBody1.put("email", "admin@mail.com");
        requestBody1.put("password", "admin");
        requestBody1.put("role", "ADMIN");
        HashMap<String, String> headersAsMap = new HashMap<>();
        headersAsMap.put("Authorization","Bearer auth-token");
        headersAsMap.put("User-Agent","Web");
        LinkedMultiValueMap mvmap = new LinkedMultiValueMap<>(headersAsMap);
        Consumer<HttpHeaders> consumer = it -> it.addAll(mvmap);
        String response1 = String.valueOf(genericClient.post(webClient, "/api/v1/auth/register",requestBody1, HashMap.class, consumer));
        System.out.println(response1);
        JsonElement jsonElement = JsonParser.parseString(response1);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String accessToken = jsonObject.get("access_token").getAsString();

        //   JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        //    localhost:8081/api/v1/auth/authenticate
        HashMap<String, String> requestBody2 = new HashMap<>();
        requestBody2.put("email", "admin@mail.com");
        requestBody2.put("password", "admin");
        HashMap<String, String> headersAsMap2 = new HashMap<>();
        headersAsMap.put("Authorization","Bearer "+accessToken.toString());
        headersAsMap.put("User-Agent","Web");
        LinkedMultiValueMap mvmap2 = new LinkedMultiValueMap<>(headersAsMap2);
        Consumer<HttpHeaders> consumer2 = it -> it.addAll(mvmap2);
        String response2 = String.valueOf(genericClient.post(webClient, "/api/v1/auth/authenticate",requestBody2, HashMap.class, consumer2));
        System.out.println(response2);
        JsonElement jsonElement2 = JsonParser.parseString(response2);
        JsonObject jsonObject2 = jsonElement2.getAsJsonObject();

        //    refresh-token -- first
        /*
        HashMap<String, String> restest = webClient.post().uri("/api/v1/auth/register")
                .body(BodyInserters.fromValue(requestBody))
                .header("Authorization", "Bearer auth-token")
                .header("User-Agent", "Web")
                .retrieve()
                .bodyToMono(HashMap.class)
                .block();
             //   .flatMap(responseEntity -> Mono.justOrEmpty(responseEntity.getBody()));
        System.out.println("Web URL" + webClient.toString());
        System.out.println("Response  ::::: " + restest.toString());
        */



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
    //    return returnval;
        return "abc";
    }

}
