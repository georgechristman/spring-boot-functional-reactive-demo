package com.reactive.person.validation;

import com.reactive.person.model.PersonDto;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PersonValidator implements Validator {

    /**
     * This Validator validates just Person instances
     */
    public boolean supports(Class clazz) {
        return PersonDto.class.equals(clazz);
    }

    public void validate(Object obj, Errors e) {
        ValidationUtils.rejectIfEmpty(e, "firstName", "field.empty");
        ValidationUtils.rejectIfEmpty(e, "lastName", "field.empty");

        PersonDto p = (PersonDto) obj;

        if (p.getFirstName().length() > 50) {
            e.rejectValue("firstName", "field.too.long");
        }

        if (p.getLastName().length() > 50) {
            e.rejectValue("lastName", "field.too.long");
        }
    }
}
