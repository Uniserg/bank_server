package com.serguni.resources;

import com.serguni.models.RegistrationForm;
import com.serguni.exceptions.IndividualRegisteredAlready;
import com.serguni.exceptions.InvalidRegistrationForm;
import com.serguni.models.ProductOrder;
import com.serguni.services.DebitCardService;
import com.serguni.services.IndividualService;
import com.serguni.services.ProductOrderService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.NoSuchElementException;


@Path("/individuals")
public class IndividualResource {

    @Inject
    IndividualService individualService;

    @Inject
    ProductOrderService productOrderService;

    @Inject
    DebitCardService debitCardService;

    @Inject
    JsonWebToken jwt;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Uni<Response> register(RegistrationForm registrationForm) {

        System.out.println("ПРИШШЕЛ ТЕЛЕФОН - :" + registrationForm.getPhoneNumber());

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
    @Path("/me/product_orders")
    @RolesAllowed("client")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<ProductOrder> getProductOrders(@QueryParam("skip") Integer skip,
                                                  @QueryParam("limit") Integer limit) {

        if (skip == null) {
            skip = 0;
        }

        if (limit == null) {
            limit = -1;
        }

        Integer finalSkip = skip;
        Integer finalLimit = limit;
        return Multi
                .createFrom()
                .items(() -> productOrderService.getAllByUserSub(jwt.getSubject(), finalSkip, finalLimit));
    }

    @GET
    @Path("/me/debit_cards")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("client")
    public Uni<Response> getDebitCards(@QueryParam("skip") Integer skip,
                                          @QueryParam("limit") Integer limit) {
        if (skip == null) {
            skip = 0;
        }

        if (limit == null) {
            limit = -1;
        }


        Integer finalSkip = skip;
        Integer finalLimit = limit;
        return Uni
                .createFrom()
                .item(() -> Response
                        .ok(debitCardService.getAllByUserSub(jwt.getSubject(), finalSkip, finalLimit))
                        .build()
                )
                .onFailure(NoSuchElementException.class)
                .recoverWithItem(f ->
                        Response
                                .status(Response.Status.NOT_FOUND)
                                .entity(f.getMessage())
                                .build()
                );
    }

    @GET
    @Path("/me/debit_cards/{card_number}/account_requisites")
    @RolesAllowed("client")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getAccountRequisites(@PathParam("card_number") String cardNumber) {
        return Uni
                .createFrom()
                .item(() -> Response
                        .ok(debitCardService.getAccountRequisites(jwt.getSubject(), cardNumber))
                        .build()
                )
                .onFailure(NoSuchElementException.class)
                .recoverWithItem(f -> Response
                                .status(Response.Status.NOT_FOUND)
                                .entity(f.getMessage())
                                .build()
                );
    }

    @GET
    @Path("/search/phone/{phone_number}")
    @RolesAllowed("client")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getProfileByPhoneNumber(@PathParam("phone_number") String phoneNumber) {
        return Uni
                .createFrom()
                .item(() -> Response
                        .ok(individualService.getProfileByPhoneNumber(phoneNumber))
                        .build()
                )
                .onFailure(NoSuchElementException.class)
                .recoverWithItem(f -> Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(f.getMessage())
                        .build()
                );
    }


    @GET
    @Path("/search/card_number/{card_number}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("client")
    public Uni<Response> getProfileByCardNumber(@PathParam("card_number") String cardNumber) {
        return Uni
                .createFrom()
                .item(() -> Response
                        .ok(individualService.getProfileByCardNumber(cardNumber)).build()
                )
              .onFailure(NoSuchElementException.class)
                .recoverWithItem(f -> Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(f.getMessage())
                        .build()
                );
    }
}
