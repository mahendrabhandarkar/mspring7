package com.ks.mspring7.proxy;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.codehaus.groovy.ast.tools.GeneralUtils.block;

public class FallBackMethodImpl {

    public Object getAPIWebClientResponse(WebClient webClient, String appUrl, Object T, Object paramT) {
        var response= webClient.get().uri(appUrl)
                .retrieve().bodyToMono(T.getClass())
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(throwable ->
                        (Mono) myFallBackMethod(paramT, throwable)
                )
                .block();

        return response;
    }

    /**
     *
     * @param webClient
     * @param appUrl
     * @param Tb --- request Body class name
     * @param Tr -- Response class name
     * @param paramT
     * @return
     */
    public Object postAPIWebClientResponse(WebClient webClient, String appUrl,
                                           Object Tb, Object Tr, Object paramT) {
        var response= webClient.post().uri(appUrl)
                .body(Mono.just(Tb), Object.class)
                .retrieve().bodyToMono(Object.class)
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(throwable ->
                        (Mono) myFallBackMethod(paramT, throwable)
                )
                .block();

        return response;
    }

/*
    private Mono<Object> myFallBackMethod(Object paramT, Throwable throwable) {
        return Mono.just("test sample");
    }
 */
    private Object myFallBackMethod(Object paramT, Throwable throwable) {
        return Mono.just("test sample");
    }
/*
    private Mono<Object> handleResponse(ClientResponse response) {

        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(Employee.class);
        }
        else if (response.statusCode().is4xxClientError()) {
            // Handle client errors (e.g., 404 Not Found)
            return Mono.error(new EmployeeNotFoundException("Employee not found"));
        }
        else if (response.statusCode().is5xxServerError()) {
            // Handle server errors (e.g., 500 Internal Server Error)
            return Mono.error(new RuntimeException("Server error"));
        }
        else {
            // Handle other status codes as needed
            return Mono.error(new RuntimeException("Unexpected error"));
        }
    }
 */
}
