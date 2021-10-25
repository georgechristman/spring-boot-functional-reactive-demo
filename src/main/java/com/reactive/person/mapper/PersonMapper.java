package com.reactive.person.mapper;

import com.reactive.person.entity.PersonEntity;
import com.reactive.person.model.PersonDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonDto personEntityToPersonDto(PersonEntity personEntity);

    PersonEntity personDtoToPersonEntity(PersonDto personDto);

}
