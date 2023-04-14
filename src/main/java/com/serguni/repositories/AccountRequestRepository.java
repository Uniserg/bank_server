package com.serguni.repositories;

import com.serguni.models.ProductOrder;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class AccountRequestRepository extends AbstractRepository {

    public void create(ProductOrder productOrder) {
        gd.g()
                .V().hasLabel("Individual")
                    .property("sub", productOrder.getUserSub())
                .as("individual")
                .addV("AccountRequest")
                    .property("address", productOrder.getAddress())
                    .property("scheduledDate", productOrder.getScheduledDate())
                    .property("status", productOrder.getStatus().getCode())
                .as("accountRequest")
                .addE("requested")
                    .property("createdAt", new Date())
                .from("individual").to("accountRequest").next();
    }

    public void confirm(long accountRequestId) {
        gd.g()
                .V(accountRequestId)
                    .property("status", ProductOrder.ProductOrderStatus.IN_PROGRESS.getCode())
                .next();
    }

//    public void complete(long accountRequestId) {
//        gd.g().tx().onReadWrite(Transaction.READ_WRITE_BEHAVIOR.AUTO).ro;
//        gd.g()
//                .V(accountRequestId)
//                    .property("status", ProductOrder.AccountRequestStatus.IN_PROGRESS.getCode())
//                .next();
//    }
}
