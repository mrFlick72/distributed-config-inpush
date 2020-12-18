package it.valeriovaudi.lab.distributedconfiginpush.repository;


import java.util.Map;

public class InMemorySampleRepository implements SampleRepository {

    private final Map<String, String> storage;

    public InMemorySampleRepository(Map<String, String> storage) {
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
