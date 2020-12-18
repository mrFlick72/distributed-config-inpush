package it.valeriovaudi.lab.distributedconfiginpush.configuration.repository;


import java.util.Map;

public class InMemoryApplicationConfigurationRepository implements ApplicationConfigurationRepository {

    private final Map<String, String> storage;

    public InMemoryApplicationConfigurationRepository(Map<String, String> storage) {
        this.storage = storage;
    }

    @Override
    public String getDataFor(String id) {
        return storage.get(id);
    }

    @Override
    public String storeDataFor(String id, String content) {
        return storage.put(id, content);
    }
}
