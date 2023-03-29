package com.serguni.resources;

import com.serguni.models.Product;
import com.serguni.services.ProductService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/products")
public class ProductResource {

    @Inject
    ProductService productService;


    @POST
    @RolesAllowed("general-manager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.WILDCARD)
    public Uni<Response> create(Product product) {
        return Uni.createFrom()
                .item(() -> Response.ok(productService.create(product)).build());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Product> getAll(@QueryParam("skip") Integer skip, @QueryParam("limit") Integer limit) {
        if (skip == null) {
            skip = 0;
        }

        if (limit == null) {
            limit = -1;
        }

        Integer finalSkip = skip;
        Integer finalLimit = limit;
        return Multi.createFrom().items(() -> productService.getAll(finalSkip, finalLimit));
    }
}
