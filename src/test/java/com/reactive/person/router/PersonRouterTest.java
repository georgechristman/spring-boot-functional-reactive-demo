package com.reactive.person.router;

import com.reactive.person.entity.PersonEntity;
import com.reactive.person.mapper.PersonMapper;
import com.reactive.person.model.PersonDto;
import com.reactive.person.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonRouterTest {

    @MockBean
    PersonRepository repository;

    @Autowired
    private WebTestClient webClient;

    @Spy
    private PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);

    @Test
    public void testCreatePerson() {
        PersonDto person = new PersonDto();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Smith");

        PersonEntity personEntity = personMapper.personDtoToPersonEntity(person);

        Mockito.when(repository.save(personEntity)).thenReturn(Mono.just(personEntity));

        webClient.post()
                .uri("/api/v1/person")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(person))
                .exchange()
                .expectStatus().isCreated();

        Mockito.verify(repository, times(1)).save(personEntity);
    }

    @Test
    void testGetPersonById() {
        PersonEntity person = new PersonEntity();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Smith");

        Mockito.when(repository.findById(1l))
                .thenReturn(Mono.just(person));

        webClient.get().uri("/api/v1/person/{id}", 1)
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.firstName").isNotEmpty()
                .jsonPath("$.lastName").isNotEmpty()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Smith");

        Mockito.verify(repository, times(1)).findById(1l);
    }

    @Test
    void testDeletePerson() {
        Mono<Void> voidReturn = Mono.empty();
        Mockito.when(repository.deleteById(1L)).thenReturn(voidReturn);

        webClient.delete().uri("/api/v1/person/{id}", 1)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testAllPersons() {
        List<PersonEntity> list = new ArrayList<>();

        PersonEntity person = new PersonEntity();
        person.setId(1L);
        person.setFirstName("John");
        person.setLastName("Smith");
        list.add(person);

        person = new PersonEntity();
        person.setId(2L);
        person.setFirstName("Jane");
        person.setLastName("Doe");
        list.add(person);

        Flux<PersonEntity> employeeFlux = Flux.fromIterable(list);

        Mockito.when(repository.findAll())
                .thenReturn(employeeFlux);

        webClient.get().uri("/api/v1/persons")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PersonEntity.class)
                .hasSize(2);

        Mockito.verify(repository, times(1)).findAll();
    }

}
