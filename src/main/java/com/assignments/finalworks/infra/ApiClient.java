package com.assignments.finalworks.infra;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class ApiClient {

    private final WebClient webClient;

    private final long port;

    public ApiClient(WebClient webClient, @Qualifier("port") long port) {
        this.webClient = webClient;
        this.port = port;
    }

    public JSONObject requestApi(long runTime, String ip) {
        long startTime = runTime - 300;
        long endTime = runTime - 60;

        String url = String.format("http://%s:%d/?type=GET_SYSTEM&query=LAST", ip, this.port);
        String query = String.format(" -s %s -e %s --resolution=60", startTime, endTime);
        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject) parser.parse(this.getData(url + query));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String getData(String url) {
        return this.webClient.mutate()
                .baseUrl(url)
                .filter(
                        ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
                                    System.out.println(">>>>>>>>>> API REQUEST URL <<<<<<<<<<");
                                    System.out.println(clientRequest.url());
                                    return Mono.just(clientRequest);
                                }
                        )
                )
                .build()
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
