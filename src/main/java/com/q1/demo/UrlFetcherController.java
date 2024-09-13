package com.q1.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class UrlFetcherController {

    private final UrlFetcherService urlFetcherService;

    public UrlFetcherController(UrlFetcherService urlFetcherService) {
        this.urlFetcherService = urlFetcherService;
    }

    @GetMapping("/fetch-urls-q1")
    public List<Object> fetchUrlsQ1(@RequestParam List<String> urls) {
        return urlFetcherService.fetchUrlsSequentially(urls);
    }

    @GetMapping("/fetch-urls-q2")
    public Mono<List<Object>> fetchUrlsQ2(@RequestParam List<String> urls) {
        return urlFetcherService.fetchUrlsConcurrently(urls);
    }
}
