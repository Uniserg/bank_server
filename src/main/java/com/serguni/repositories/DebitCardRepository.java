package com.serguni.repositories;

import com.serguni.models.DebitCard;
import com.serguni.models.Individual;
import com.serguni.utils.CamelCaseObjectMapperUtil;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

@ApplicationScoped
public class DebitCardRepository extends AbstractRepository {

    public Stream<DebitCard> getAllByUserSub(String userSub) {
        var cards = gd.g
                .V()
                .match(
                        as("individual").has(Individual.class.getSimpleName(), "sub", userSub),
                        as("individual").out("OWNS").as("card"),
                        as("card").out("ASSOCIATED_WITH").as("product")
                )
                .project("productName", "number", "holderName", "expirationDate", "cvv", "isActive")
                .by(select("product").values("name"))
                .by(select("card").values("number"))
                .by(select("card").values("holderName"))
                .by(select("card").values("expirationDate"))
                .by(select("card").values("cvv"))
                .by(select("card").values("isActive"))
                .toStream();

        return cards.map((c) -> CamelCaseObjectMapperUtil.convertValue(c, DebitCard.class));
    }
}
