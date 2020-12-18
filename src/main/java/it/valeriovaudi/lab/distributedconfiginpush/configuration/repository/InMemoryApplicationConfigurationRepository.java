package it.valeriovaudi.lab.distributedconfiginpush.configuration.repository;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public class InMemoryApplicationConfigurationRepository implements ApplicationConfigurationRepository {

    private final Map<String, String> storage;

    public InMemoryApplicationConfigurationRepository(Map<String, String> storage) {
        this.storage = storage;
    }

    @Override
    public Flux<Map.Entry<String, String>> getData() {
        return Flux.defer(() -> Flux.fromIterable(storage.entrySet()));
    }

    @Override
    public Mono<String> getDataFor(String id) {
        return Mono.defer(() -> {
            return Mono.just(storage.get(id));
        });
    }

    @Override
    public Mono<Void> storeDataFor(String id, String content) {
        return Mono.defer(() -> {
            storage.put(id, content);
            return Mono.empty();
        });
    }
}
