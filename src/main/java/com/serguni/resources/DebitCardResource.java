package com.serguni.resources;

import com.serguni.models.AccountRequisites;
import com.serguni.models.Transfer;
import com.serguni.notifications.TransferNotificationListener;
import com.serguni.services.DebitCardService;
import com.serguni.services.IndividualService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

@Path("/debit_cards")
public class DebitCardResource {

    @Inject
    DebitCardService debitCardService;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("/{card_number}/account_requisites")
    @RolesAllowed("admin")
    public Uni<AccountRequisites> getAccountRequisites(@PathParam("card_number") String cardNumber) {
        return Uni
                .createFrom()
                .item(() -> debitCardService.getAccountRequisitesByNumber(cardNumber));
    }

    @POST
    @Path("/transfer")
    @RolesAllowed("client")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> createTransfer(Transfer transfer) {

        transfer.setCreatedAt(new Date());
        transfer.setSessionSub(jwt.getClaim("sid"));

        return Uni
                .createFrom()
                .item(() -> {
                    debitCardService.createTransfer(jwt.getSubject(), transfer);
                    return Response.ok(transfer).build();
                });
    }

    @GET
    @Path("/{card_number}/account_operations")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("client")
    public Uni<Response> getAllTransfers(
            @PathParam("card_number") String cardNumber,
            @QueryParam("skip") Integer skip,
            @QueryParam("limit") Integer limit
    ) {

        if (skip == null) {
            skip = 0;
        }

        if (limit == null) {
            limit = -1;
        }

        int finalSkip = skip;
        int finalLimit = limit;

        System.out.println(skip);
        System.out.println(limit);

        return Uni.createFrom()
                .item(() -> Response.ok(
                        debitCardService
                                .getAllTransfers(jwt.getSubject(), cardNumber, finalSkip, finalLimit)
                ).build())
                .onFailure(NoSuchElementException.class)
                .recoverWithItem(
                        Response.status(Response.Status.NOT_FOUND).build());

//        return Multi
//                .createFrom()
//                .items(() -> debitCardService
//                        .getAllTransfers(jwt.getSubject(), cardNumber, finalSkip, finalLimit));

//        return Multi
//                .createFrom()
//                .items(() -> debitCardService.getAllTransfers(jwt.getSubject(), cardNumber, finalSkip, finalLimit))
//                .collect().asList()
//                .map((e) -> Response.ok(e).build())
//                .onFailure(NoSuchElementException.class)
//                .recoverWithItem(Response.status(Response.Status.NOT_FOUND).build());
    }

}
