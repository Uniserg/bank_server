package com.serguni.repositories;

import com.serguni.models.ProductRequest;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class AccountRequestRepository extends AbstractRepository {

    public void create(ProductRequest productRequest) {
        gd.g
                .V().hasLabel("Individual")
                    .property("sub", productRequest.getUserSub())
                .as("individual")
                .addV("AccountRequest")
                    .property("address", productRequest.getAddress())
                    .property("scheduledDate", productRequest.getScheduledDate())
                    .property("status", productRequest.getStatus().getCode())
                .as("accountRequest")
                .addE("requested")
                    .property("createdAt", new Date())
                .from("individual").to("accountRequest").next();
    }

    public void confirm(long accountRequestId) {
        gd.g
                .V(accountRequestId)
                    .property("status", ProductRequest.ProductRequestStatus.IN_PROGRESS.getCode())
                .next();
    }

//    public void complete(long accountRequestId) {
//        gd.g.tx().onReadWrite(Transaction.READ_WRITE_BEHAVIOR.AUTO).ro;
//        gd.g
//                .V(accountRequestId)
//                    .property("status", ProductRequest.AccountRequestStatus.IN_PROGRESS.getCode())
//                .next();
//    }
}
