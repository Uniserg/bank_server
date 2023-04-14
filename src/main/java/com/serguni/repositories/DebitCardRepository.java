package com.serguni.repositories;

import com.serguni.models.Account;
import com.serguni.models.DebitCard;
import com.serguni.models.Individual;
import com.serguni.models.Transfer;
import com.serguni.models.requisites.BIK;
import com.serguni.models.requisites.CurrentAccount;
import com.serguni.utils.CamelCaseObjectMapperUtil;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.function.Lambda;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.asc;
import static org.apache.tinkerpop.gremlin.process.traversal.Order.desc;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

@ApplicationScoped
public class DebitCardRepository extends AbstractRepository {

    @Inject
    MyBankRepository myBankRepository;

    public Stream<DebitCard> getAllByUserSub(String userSub, int skip, int limit) {
        var cards = gd.g()
                .V()
                .match(
                        as("individual").has(Individual.class.getSimpleName(), "sub", userSub),
                        as("individual").out("OWNS").as("card"),
                        as("card").out("ASSOCIATED_WITH").as("product"),
                        as("card").out("MANAGES").as("account")
                )
                .project("productName", "number", "holderName", "expirationDate", "cvv", "isActive", "balance")
                .by(select("product").values("name"))
                .by(select("card").values("number"))
                .by(select("card").values("holderName"))
                .by(select("card").values("expirationDate"))
                .by(select("card").values("cvv"))
                .by(select("card").values("isActive"))
                .by(select("account").values("balance"))
                .skip(skip)
                .limit(limit)
                .toStream();

        return cards.map((c) -> CamelCaseObjectMapperUtil.convertValue(c, DebitCard.class));
    }
    public Account getAccountByNumber(String number) {
        var accountMap = gd.g()
                .V()
                .has(DebitCard.class.getSimpleName(), "number", number)
                .out("MANAGES")
                .elementMap().next();

        return getAccount(accountMap);
    }

    private Account getAccount(Map<Object, Object> accountMap) {

        Account account = new Account();

        BIK bik = BIK.parse(myBankRepository.getMyBank().getBik());
        CurrentAccount accountNumber = CurrentAccount.parse((String) accountMap.get("number"), bik);

        account.setNumber(accountNumber);
        account.setBalance((Float) accountMap.get("balance"));
        account.setActive((Boolean) accountMap.get("isActive"));

        return account;
    }

    public Account getAccount(String userSub, String cardNumber) {
        var accountMap = gd.g()
                .V()
                .match(
                        as("individual").has(Individual.class.getSimpleName(), "sub", userSub),
                        as("individual").out("OWNS").as("owned"),
                        as("owned").has(DebitCard.class.getSimpleName(), "number", cardNumber).as("debitCard"),
                        as("debitCard").out("MANAGES").as("account")
                )
                .select("account")
                .elementMap()
                .next();

        return getAccount(accountMap);
    }

    public boolean isBalanceEnough(String cardNumber, float amount) {
        float balance = (float) gd.g()
                .V()
                .has(DebitCard.class.getSimpleName(), "number", cardNumber)
                .values("balance").next();

        return balance >= amount;
    }

    public boolean isOwnedCard(String cardNumber, String userSub) {
        return  gd.g()
                .V().match(
                as("individual").has(Individual.class.getSimpleName(), "sub", userSub),
                as("individual").out("OWNS")
                        .has(DebitCard.class.getSimpleName(), "number", cardNumber).as("card")
        ).hasNext();
    }

    public void createTransfer(String userSub, Transfer transfer) {
//
//        Transaction tx = gd.g().tx();
//
//// spawn a GraphTraversalSource from the Transaction. Traversals spawned
//// from gtx will be essentially be bound to tx
//        GraphTraversalSource gtx = tx.begin();
//
//        try {
//
//            tx.commit();
//        } catch (Exception e) {
//            tx.rollback();
//        }

        gd.g()
                .V()
                    .has(Individual.class.getSimpleName(), "sub", userSub)
                .as("individual")
                    .out("OWNS")
                    .has(DebitCard.class.getSimpleName(), "number", transfer.getCardNumberFrom())
                .as("cardFrom")
                    .out("MANAGES").has(Account.class.getSimpleName(), "balance", P.gte(transfer.getAmount()))
                .as("accountFrom")
                .V()
                    .has(DebitCard.class.getSimpleName(), "number", transfer.getCardNumberTo())
                .as("cardTo")
                    .out("MANAGES")
                .as("accountTo")
                .addE("TRANSFER")
                    .property("createdAt", transfer.getCreatedAt())
                    .property("amount", transfer.getAmount())
                    .property("sessionSub", transfer.getSessionSub())
                    .property("message", transfer.getMessage())
                .as("transfer")
                .from("cardFrom")
                .to("cardTo")
                .select("accountFrom")
                    .property("balance", union(values("balance"), constant(-transfer.getAmount())).sum())
                .select("accountTo")
                    .property("balance", union(values("balance"), constant(transfer.getAmount())).sum())
                .next();
    }


    public GraphTraversal<Vertex, Map<String, Object>> matchUserOwnsCard(String userSub, String cardNumber) {
        return gd.g()
                .V()
                .match(
                        as("individual").out("OWNS").as("card"),
                        as("individual").has(Individual.class.getSimpleName(), "sub", userSub),
                        as("card").has(DebitCard.class.getSimpleName(), "number", cardNumber)
                );
    }

    public Stream<Transfer> getAllTransfers(String userSub, String cardNumber, int skip, int limit) {
        Function<Traverser<Vertex>, String> f =
                Lambda.function("{it.get().value(\"firstName\") + \" \" + it.get().value(\"lastName\").substring(0, 1) + \".\"}");

        return matchUserOwnsCard(userSub, cardNumber)
                        .match(
                                as("card").bothE("TRANSFER").as("transfer"),
                                as("transfer").inV().as("cardTo"),
                                as("transfer").outV().as("cardFrom"),
                                as("cardFrom").in("OWNS").map(f).as("nameFrom"),
                                as("cardTo").in("OWNS").map(f).as("nameTo")
                        )
                        .order()
                        .by(select("transfer").values("createdAt"), desc)
                .project("createdAt", "amount", "sessionSub", "message", "cardNumberFrom", "cardNumberTo", "nameFrom", "nameTo")
                    .by(select("transfer").values("createdAt"))
                    .by(select("transfer").values("amount"))
                    .by(select("transfer").values("sessionSub"))
                    .by(select("transfer").values("message"))
                    .by(select("cardFrom").values("number"))
                    .by(select("cardTo").values("number"))
                    .by(select("nameFrom"))
                    .by(select("nameTo"))
                .skip(skip)
                .limit(limit)
                .toStream()
                .map((t) -> CamelCaseObjectMapperUtil.convertValue(t, Transfer.class));
    }


    public long createNewCard(long productId, Account account, DebitCard card) {
        return (long) gd.g()
                .V(productId).out("ASSOCIATED_WITH").as("product")
                .addV("Account")
                    .property("number", account.getNumber().toString())
                    .property("balance", account.getBalance())
                    .property("isActive", account.isActive())
                .as("account")
                .addV("DebitCard")
                    .property("number", card.getNumber())
                    .property("holderName", card.getHolderName())
                    .property("expirationDate", card.getExpirationDate())
                    .property("cvv", card.getCvv())
                    .property("isActive", card.isActive())
                .as("card")
                .addE("MANAGES").from("card").to("account")
                .property("createdAt", new Date())
                .addE("ASSOCIATED_WITH").from("card").to("product")
                .select("card")
                .values(T.id.getAccessor())
                .next();
    }

    public long reserveFreeCard(String holderName, Date expirationDate) {

        var t = gd.g()
                .V()
                .hasLabel("Free")
                .inE("IN")
                .order()
                .by("createdAt", asc)
                .limit(1).as("free")
                .outV().as("card");

        long cardId = (long) t.values(T.id.getAccessor()).next();

        gd.g().V(cardId)
                .property("holderName", holderName)
                .property("expirationDate", expirationDate)
                .outE("IN").drop().iterate();

        return cardId;
    }

    public void createReserveEdge(long productRequestId, long debitCardId) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 7);
        Date expirationDate = c.getTime();

        gd.g()
                .V(productRequestId).as("request")
                .V(debitCardId).as("card")
                .addE("RESERVES")
                .from("request")
                .to("card")
                .property("createdAt", new Date())
                .property("expiresAt", expirationDate)
                .next();
    }

    public Stream<String> getAllCardNumbersByUserSub(String userSub) {
        return gd.g()
                .V()
                .has(Individual.class.getSimpleName(), "sub", userSub)
                .out("OWNS")
                .hasLabel(DebitCard.class.getSimpleName())
                .values("number")
                .toStream()
                .map((c) -> (String) c);
    }
}
