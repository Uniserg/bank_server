package com.serguni.repositories;


import com.serguni.models.Individual;
import com.serguni.utils.CamelCaseObjectMapperUtil;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IndividualRepository extends AbstractRepository {

    public void create(Individual individual) {
        gd.g
            .addV("Individual")
            .property("sub", individual.getSub())
            .property("lastName", individual.getLastName())
            .property("firstName", individual.getFirstName())
            .property("middleName", individual.getMiddleName())
            .property("passport", individual.getPassport())
            .property("inn", individual.getInn()).next();
    }

    public Individual getBySub(String individualSub) {

        var individualMap = gd.g
                .V()
                .has("sub", individualSub)
                .elementMap()
                .next();

        return CamelCaseObjectMapperUtil.convertValue(individualMap, Individual.class);
    }
}
