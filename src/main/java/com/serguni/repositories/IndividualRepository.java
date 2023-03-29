package com.serguni.repositories;


import com.serguni.models.DebitCard;
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
            .property("phoneNumber", individual.getPhoneNumber())
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

    private Individual findBy(String field, String value) {

        var individualMap = gd.g
                .V()
                .has("Individual", field, value)
                .elementMap().next();

        return CamelCaseObjectMapperUtil.convertValue(individualMap, Individual.class);
    }

    public Individual findByEmail(String email) {
        return findBy("email", email);
    }

    public Individual findByPhoneNumber(String phoneNumber) {
        return findBy("phoneNumber", phoneNumber);
    }

    public Individual findByCardNumber(String cardNumber) {

        var individualMap = gd.g
                .V()
                .has(DebitCard.class.getSimpleName(), "number", cardNumber)
                .in("OWNS")
                .elementMap().next();

        return CamelCaseObjectMapperUtil.convertValue(individualMap, Individual.class);
    }

}
