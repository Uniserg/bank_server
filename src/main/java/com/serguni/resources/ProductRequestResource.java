package com.serguni.resources;

import com.serguni.models.ProductRequest;
import com.serguni.services.ProductRequestService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("product_requests")
public class ProductRequestResource {
    @Inject
    ProductRequestService productRequestService;

    @POST
    public Uni<ProductRequest> create(ProductRequest productRequest) {
        return Uni.createFrom()
                .item(() -> productRequestService.create(productRequest));
    }

    @POST
    @Path("/{product_request_id}/confirm")
    public Response confirm(@PathParam("product_request_id") long productRequestId) {
        productRequestService.confirm(productRequestId);
        return Response.ok().build();
    }

    @POST
    @Path("/{product_request_id}/complete")
    public Response complete(@PathParam("product_request_id") long productRequestId) {
        productRequestService.complete(productRequestId);
        return Response.ok().build();
    }
}
