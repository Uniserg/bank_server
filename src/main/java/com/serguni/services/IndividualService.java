package com.serguni.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.serguni.clients.KeycloakAdminClient;
import com.serguni.models.RegistrationForm;
import com.serguni.exceptions.InvalidRegistrationForm;
import com.serguni.models.Individual;
import com.serguni.models.Profile;
import com.serguni.repositories.IndividualRepository;
import com.serguni.utils.CamelCaseObjectMapperUtil;
import com.serguni.utils.EmailValidationUtil;
import com.serguni.utils.KeycloakRegisterUtil;
import com.serguni.utils.PhoneValidationNumberUtil;
import com.serguni.vars.KeycloakProps;

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

    @Inject
    KeycloakAdminClient keycloakAdminClient;

    @Inject
    KeycloakProps keycloakProps;

    private String checkPhoneNumber(String phoneNumber) {
        String errMsg = "Invalid phone number.\n";

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
            return "Invalid email.";
        }
        return "";
    }

    public Individual register(RegistrationForm registrationForm) {

        // TODO: сделать проверки на все уникальные поля - паспорт, инн, телефон и т.д.
        System.out.println(registrationForm);

        String errMsg = checkEmail(registrationForm.getEmail()) +
                checkPhoneNumber(registrationForm.getPhoneNumber());
        System.out.println(errMsg);

        if (!errMsg.isEmpty()) {
            throw new InvalidRegistrationForm(errMsg);
        }
        String sub = keycloakRegisterUtil.register(registrationForm);

        Individual individual = om.convertValue(registrationForm, Individual.class);
        individual.setSub(sub);

        individualRepository.create(individual);
        return individual;
    }

    public Individual getBySub(String sub) {
        return individualRepository.getBySub(sub);
    }

    public Profile getProfileByPhoneNumber(String phoneNumber) {
        Individual individual = individualRepository.findByPhoneNumber(phoneNumber);

        String email = keycloakAdminClient.getKeycloak().realm(keycloakProps.realm())
                .users()
                .get(individual.getSub())
                .toRepresentation()
                .getEmail();
        individual.setEmail(email);

        return CamelCaseObjectMapperUtil.convertValue(individual, Profile.class);
    }

    public Profile getProfileByEmail(String email) {
        return individualRepository.findByEmail(email);
    }

    public Profile getProfileByCardNumber(String cardNumber) {

        Individual individual = individualRepository.findByCardNumber(cardNumber);
        String email = keycloakAdminClient.getKeycloak().realm(keycloakProps.realm())
                .users()
                .get(individual.getSub())
                .toRepresentation()
                .getEmail();
        individual.setEmail(email);

        return CamelCaseObjectMapperUtil.convertValue(individual, Profile.class);
    }

}
