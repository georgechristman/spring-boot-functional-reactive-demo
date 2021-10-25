package com.reactive.person.handler;

import com.reactive.person.entity.PersonEntity;
import com.reactive.person.mapper.PersonMapper;
import com.reactive.person.model.PersonDto;
import com.reactive.person.repository.PersonRepository;
import com.reactive.person.validation.PersonValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Slf4j
@RequiredArgsConstructor
public class PersonHandler {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    private final Validator validator = new PersonValidator();

    public Mono<ServerResponse> all(ServerRequest req) {
        Flux<PersonDto> people = personRepository.findAll().map(personMapper::personEntityToPersonDto);
        return ok().contentType(APPLICATION_JSON).body(people, PersonDto.class);
    }

    public Mono<ServerResponse> get(ServerRequest req) {
        return personRepository.findById(getId(req))
                .map(personMapper::personEntityToPersonDto)
                .flatMap(personDto -> ok()
                    .contentType(APPLICATION_JSON)
                    .bodyValue(personDto))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest req)  {
        return req.bodyToMono(PersonDto.class).doOnNext(this::validate)
                .map(personMapper::personDtoToPersonEntity)
                .flatMap(personRepository::save)
                .flatMap(personEntity ->
                        created(URI.create("/api/v1/person/" + personEntity.getId())).build()
                );
    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        return noContent().build(personRepository.deleteById(getId(req)));
    }

    public Mono<ServerResponse> update(ServerRequest req) {
        final Mono<PersonDto> personDtoMono = req.bodyToMono(PersonDto.class).doOnNext(this::validate);

        return personRepository.findById(getId(req))
                .flatMap(personEntity -> ok()
                        .contentType(APPLICATION_JSON)
                        .body(fromPublisher(personDtoMono
                                .map(personDto -> new PersonEntity(personEntity.getId(), personDto.getFirstName(), personDto.getLastName()))
                                .flatMap(personEntity1 -> personRepository.save(personEntity1))
                                .map(personMapper::personEntityToPersonDto), PersonDto.class)))
                        .switchIfEmpty(notFound().build());
    }

    private long getId(ServerRequest req) {
        return Long.valueOf(req.pathVariable("id"));
    }

    private void validate(PersonDto personDto) {
        Errors errors = new BeanPropertyBindingResult(personDto, "personDto");
        validator.validate(personDto, errors);
        if (errors.hasErrors()) {
            throw new ServerWebInputException(errors.toString());
        }
    }

}
