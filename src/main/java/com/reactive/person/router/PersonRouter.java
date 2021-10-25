package com.reactive.person.router;

import com.reactive.person.handler.PersonHandler;
import com.reactive.person.mapper.PersonMapper;
import com.reactive.person.repository.PersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PersonRouter {

    static RequestPredicate accept = accept(MediaType.APPLICATION_JSON);

    @Bean
    public PersonHandler personHandler(final PersonRepository personRepository,
                                       final PersonMapper personMapper) {
        return new PersonHandler(personRepository, personMapper);
    }

    @Bean
    public RouterFunction<ServerResponse> personRoute(final PersonHandler personHandler){
        return RouterFunctions
                .route(GET("/api/v1/persons").and(accept), personHandler::all)
                .andRoute(GET("/api/v1/person/{id}").and(accept), personHandler::get)
                .andRoute(POST("/api/v1/person").and(accept), personHandler::create)
                .andRoute(DELETE("/api/v1/person/{id}").and(accept), personHandler::delete)
                .andRoute(PUT("/api/v1/person/{id}").and(accept), personHandler::update);
    }

}
