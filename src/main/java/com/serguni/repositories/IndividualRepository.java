package com.serguni.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serguni.GraphDriver;
import com.serguni.models.Individual;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class IndividualRepository {

    @Inject
    GraphDriver gd;

    @Inject
    ObjectMapper om;

    public Individual create(Individual individual) {
        gd.g
            .addV("Individual")
            .property("sub", individual.getSub())
            .property("lastName", individual.getLastName())
            .property("firstName", individual.getFirstName())
            .property("middleName", individual.getMiddleName())
            .property("passport", individual.getPassport())
            .property("inn", individual.getInn()).next();

        return individual;
    }

    public Individual getById(long individualId) {
        return gd.g
                .V(individualId)
                .elementMap()
                .toStream()
                .map(c -> om.convertValue(c, Individual.class))
                .findFirst()
                .orElse(null);
    }
}
