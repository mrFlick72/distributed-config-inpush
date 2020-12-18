package it.valeriovaudi.lab.distributedconfiginpush.endpoint;

import it.valeriovaudi.lab.distributedconfiginpush.configuration.repository.ApplicationConfigurationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ConfigurationEndpoint {

    @Bean
    public RouterFunction configurationEndpointRoute(ApplicationConfigurationRepository repository) {
        return RouterFunctions.route()
                .GET(
                        "/application-configurations",
                        serverRequest -> repository.getData().collectList()
                                .flatMap(datas -> ServerResponse.ok()
                                        .body(BodyInserters.fromValue(datas)))
                )
                .GET(
                        "/application-configurations/{key}",
                        serverRequest -> repository.getDataFor(serverRequest.pathVariable("key"))
                                .flatMap(data -> ServerResponse.ok()
                                        .body(BodyInserters.fromValue(data)))
                )
                .build();
    }
}
