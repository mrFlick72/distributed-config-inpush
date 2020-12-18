package it.valeriovaudi.lab.distributedconfiginpush.configuration.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ApplicationConfigurationRepository {

    Flux<Map.Entry<String, String>> getData();

    Mono<String> getDataFor(String id);

    Mono<Void> storeDataFor(String id, String content);
}

