package com.serguni.resources;

import com.serguni.dto.RegistrationForm;
import com.serguni.exceptions.IndividualRegisteredAlready;
import com.serguni.exceptions.InvalidRegistrationForm;
import com.serguni.services.IndividualService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/individuals")
public class IndividualResource {

    @Inject
    IndividualService individualService;

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
}
