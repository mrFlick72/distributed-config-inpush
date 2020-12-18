package it.valeriovaudi.lab.distributedconfiginpush.repository;

public interface SampleRepository {

    String getDataFor(String id);

    String storeDataFor(String id, String content);
}

