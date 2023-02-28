package com.serguni.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.serguni.dto.RegistrationForm;
import com.serguni.exceptions.IndividualRegisteredAlready;
import com.serguni.exceptions.InvalidRegistrationForm;
import com.serguni.exceptions.RegistrationFailed;
import com.serguni.models.Individual;
import com.serguni.repositories.IndividualRepository;
import com.serguni.utils.EmailValidationUtil;
import com.serguni.utils.KeycloakRegisterUtil;
import com.serguni.utils.PhoneValidationNumberUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class IndividualService {
    @Inject
    ObjectMapper om;
    @Inject
    KeycloakRegisterUtil keycloakRegisterUtil;
    @Inject
    IndividualRepository individualRepository;

    private String checkPhoneNumber(String phoneNumber) {
        String errMsg = "Неверный номер телефона.\n";

        try {
            if (!PhoneValidationNumberUtil.isPhoneNumberValid(phoneNumber)) {
                return errMsg;
            }
        } catch (NumberParseException e) {
            return errMsg;
        }
        return "";
    }

    private String checkEmail(String email) {
        if (!EmailValidationUtil.isEmailValid(email)) {
            return "Неверный email.";
        }
        return "";
    }

    public Individual register(RegistrationForm registrationForm) {
        String errMsg = checkEmail(registrationForm.getEmail()) +
                checkPhoneNumber(registrationForm.getPhoneNumber());

        if (!errMsg.isEmpty()) {
            throw new InvalidRegistrationForm(errMsg);
        }
        String sub = keycloakRegisterUtil.register(registrationForm);

        Individual individual = om.convertValue(registrationForm, Individual.class);
        individual.setSub(sub);

        return individualRepository.create(individual);
    }
}