package com.serguni.resources;

import com.serguni.dto.RegistrationForm;
import com.serguni.exceptions.IndividualRegisteredAlready;
import com.serguni.exceptions.InvalidRegistrationForm;
import com.serguni.models.DebitCard;
import com.serguni.models.ProductRequest;
import com.serguni.services.DebitCardService;
import com.serguni.services.IndividualService;
import com.serguni.services.ProductRequestService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/individuals")
public class IndividualResource {

    @Inject
    IndividualService individualService;

    @Inject
    ProductRequestService productRequestService;

    @Inject
    DebitCardService debitCardService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Uni<Response> register(RegistrationForm registrationForm) {
        return Uni
                .createFrom()
                .item(() -> Response
                        .status(Response.Status.CREATED)
                        .entity(individualService.register(registrationForm))
                        .build())
                .onFailure(IndividualRegisteredAlready.class)
                .recoverWithItem(f ->
                        Response
                                .status(Response.Status.CONFLICT)
                                .entity(f.getMessage())
                                .build())
                .onFailure(InvalidRegistrationForm.class)
                .recoverWithItem(f ->
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(f.getMessage())
                                .build());
//                .onFailure()
//                .recoverWithItem(Response
//                        .status(Response.Status.INTERNAL_SERVER_ERROR)
//                        .entity("Не удалось зарегистрироваться, ошибка с нашей стороны. Приносим свои извинения.")
//                        .build());
    }

    @GET
    @Path("/{user_sub}/product_requests")
    public Multi<ProductRequest> getProductRequests(@PathParam("user_sub") String userSub,
                                                    @QueryParam("skip") Integer skip,
                                                    @QueryParam("limit") Integer limit) {

        if (skip == null) {
            skip = 0;
        }

        if (limit == null) {
            limit = -1;
        }

        return Multi.createFrom().items(productRequestService.getAllByUserSub(userSub, skip, limit));
    }

    @GET
    @Path("/{user_sub}/debit_cards")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Multi<DebitCard> getDebitCards(@PathParam("user_sub") String userSub,
                                          @QueryParam("skip") Integer skip,
                                          @QueryParam("limit") Integer limit) {
        if (skip == null) {
            skip = 0;
        }

        if (limit == null) {
            limit = -1;
        }

        return Multi.createFrom().items(debitCardService.getAllByUserSub(userSub, skip, limit));
    }
}
