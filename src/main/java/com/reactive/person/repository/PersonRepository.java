package com.reactive.person.repository;

import com.reactive.person.entity.PersonEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PersonRepository extends ReactiveCrudRepository<PersonEntity, Long> {
}
