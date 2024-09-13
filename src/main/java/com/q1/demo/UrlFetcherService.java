package com.q1.demo;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;

@Service
public class UrlFetcherService {

    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(UrlFetcherService.class);

    public UrlFetcherService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Object> fetchUrlsSequentially(List<String> urls) {
        List<Object> responses = new ArrayList<>();
        Object previousResponse = null;

        for (String url : urls) {
            try {
                Object response = fetchDataFromUrl(url, previousResponse);
                responses.add(response);
                previousResponse = response;
            } catch (Exception e) {
                logger.error("Error fetching data from URL: " + url, e);
                responses.add(null);
                previousResponse = null;
            }
        }
        return responses;
    }

    private Object fetchDataFromUrl(String url, Object previousResponse) {
        return webClient.post()
                .uri(url)
                .body(Mono.justOrEmpty(previousResponse), Object.class)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Mono<List<Object>> fetchUrlsConcurrently(List<String> urls) {
        // Sử dụng Flux để xử lý các URLs đồng thời
        return Flux.fromIterable(urls)
                .flatMap(this::fetchDataFromUrl)
                .collectList();  // Thu thập kết quả thành List
    }

    private Mono<Object> fetchDataFromUrl(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorResume(e -> {
                    logger.error("Error fetching data from URL: " + url, e);
                    return Mono.just(null);  // Trả về null nếu có lỗi
                });
    }
}
