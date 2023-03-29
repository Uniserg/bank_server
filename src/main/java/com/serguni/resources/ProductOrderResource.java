package com.serguni.resources;

import com.serguni.models.ProductOrder;
import com.serguni.services.ProductOrderService;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("product_orders")
public class ProductOrderResource {
    @Inject
    ProductOrderService productOrderService;
    @Inject
    JsonWebToken jwt;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("client")
    public Uni<ProductOrder> create(ProductOrder productOrder) {
        productOrder.setUserSub(jwt.getSubject());

        return Uni.createFrom()
                .item(() -> productOrderService.create(productOrder));
    }

    @POST
    @Path("/{product_request_id}/confirm")
    @RolesAllowed("operator")
    public Uni<Response> confirm(@PathParam("product_request_id") long productOrderId) {
        return Uni.createFrom().item(() -> {
            productOrderService.confirm(productOrderId);
            return Response.ok().build();
        });
    }

    @POST
    @Path("/{product_request_id}/complete")
    @RolesAllowed("operator")
    public Uni<Response> complete(@PathParam("product_request_id") long productOrderId) {
        return Uni.createFrom().item(() -> {
            productOrderService.complete(productOrderId);
            return Response.ok().build();
        });
    }

    @POST
    @Path("/{product_request_id}/refuse")
    @RolesAllowed("operator")
    public Uni<Response> refuse(@PathParam("product_request_id") long productOrderId) {
        return Uni.createFrom().item(() -> {
            productOrderService.refuse(productOrderId);
            return Response.ok().build();
        });
    }
}
