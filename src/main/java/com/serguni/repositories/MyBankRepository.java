package com.serguni.repositories;

import com.serguni.models.MyBank;
import com.serguni.utils.CamelCaseObjectMapperUtil;

import javax.enterprise.context.ApplicationScoped;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

@ApplicationScoped
public class MyBankRepository extends AbstractRepository {

    public void create(MyBank myBank) {
        gd.g().addV("MyBank")
                .property("name", myBank.getName())
                .property("correspondAccount", myBank.getCorrespondAccount())
                .property("bik", myBank.getBik())
                .property("inn", myBank.getInn())
                .property("kpp", myBank.getKpp())
                .property("accountsCount", myBank.getAccountsCount())
                .next();
    }

    public long incrAccountsCount() {
        return (long) gd.g().V()
                .hasLabel("MyBank")
                .property("accountsCount",
                        union(values("accountsCount"), constant(1)).sum())
                .values("accountsCount").next();
    }

    public MyBank getMyBank() {
        var myBankMap = gd.g()
                .V()
                .hasLabel(MyBank.class.getSimpleName())
                .elementMap()
                .next();

        return CamelCaseObjectMapperUtil.convertValue(myBankMap, MyBank.class);
    }
}
