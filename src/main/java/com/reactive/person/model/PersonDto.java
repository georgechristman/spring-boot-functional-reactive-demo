package com.reactive.person.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;

}
