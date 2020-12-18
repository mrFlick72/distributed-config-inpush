package it.valeriovaudi.lab.distributedconfiginpush.configuration.repository;

public interface ApplicationConfigurationRepository {

    String getDataFor(String id);

    String storeDataFor(String id, String content);
}

