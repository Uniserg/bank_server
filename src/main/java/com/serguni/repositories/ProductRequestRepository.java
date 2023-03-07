package com.serguni.repositories;

import com.serguni.models.Account;
import com.serguni.models.DebitCard;
import com.serguni.models.ProductRequest;
import com.serguni.utils.CamelCaseObjectMapperUtil;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;


@ApplicationScoped
public class ProductRequestRepository extends  AbstractRepository {

    public long create(ProductRequest productRequest) {
        return (long) gd.g
                .V()
                    .hasLabel("Individual")
                    .has("sub", productRequest.getUserSub())
                    .as("individual")
                .V()
                    .hasLabel("Product")
                    .has("name", productRequest.getProductName())
                .as("product")
                .addV("ProductRequest")
                    .property("address", productRequest.getAddress())
                    .property("scheduledDate", productRequest.getScheduledDate())
                    .property("status", productRequest.getStatus().getCode())
                .as("request")
                .addE("MAKE_REQUEST")
                    .from("individual").to("request")
                    .property("createdAt", new Date())
                .addE("ASSOCIATED_WITH")
                    .from("request").to("product")
                .select("request")
                .values(T.id.getAccessor())
                .next();
    }

    public Stream<ProductRequest> getAllByStatus(ProductRequest.ProductRequestStatus status, int skip, int limit) {

        var traverse = gd.g.V()
                .match(
                        as("productRequest").has(ProductRequest.class.getSimpleName(), "status", status.getCode()),
                        as("productRequest").inE("MAKE_REQUEST").as("makeRequest"),
                        as("makeRequest").outV().as("individual"),
                        as("productRequest").out("ASSOCIATED_WITH").as("product")
                );

        return makeProjection(traverse)
                .skip(skip)
                .limit(limit)
                .toStream()
                .map((pr) -> CamelCaseObjectMapperUtil.convertValue(pr, ProductRequest.class));

    }

    public Stream<ProductRequest> getAllByUserSub(String userSub, int skip, int limit) {

        var traverse = gd.g.V()
                .match(
                        as("individual").has("Individual","sub", userSub),
                        as("individual").outE("MAKE_REQUEST").as("makeRequest"),
                        as("makeRequest").inV().as("productRequest"),
                        as("productRequest").out("ASSOCIATED_WITH").as("product")
                );

        return makeProjection(traverse)
                .skip(skip)
                .limit(limit)
                .toStream()
                .map((pr) -> CamelCaseObjectMapperUtil.convertValue(pr, ProductRequest.class));
    }

    private GraphTraversal<Vertex, Map<String, Object>> makeProjection(GraphTraversal<Vertex, Map<String, Object>> graphTraversal) {
        return graphTraversal
                .project("id","userSub","createdAt", "productName", "address", "scheduledDate", "status")
                .by(select("productRequest").values(T.id.getAccessor()))
                .by(select("individual").values("sub"))
                .by(select("makeRequest").values("createdAt"))
                .by(select("product").values("name"))
                .by(select("productRequest").values("address"))
                .by(select("productRequest").values("scheduledDate"))
                .by(select("productRequest").values("status"));
    }

    public ProductRequest getById(long id) {
        var traverse = gd.g
                .V(id).as("productRequest")
                .match(
                    as("productRequest").inE("MAKE_REQUEST").as("makeRequest"),
                    as("makeRequest").outV().as("individual"),
                    as("productRequest").out("ASSOCIATED_WITH").as("product")
                );
        return CamelCaseObjectMapperUtil.convertValue(makeProjection(traverse).next(), ProductRequest.class);
    }

    public void confirm(long productRequestId) {
        gd.g.V(productRequestId)
                .property("status", ProductRequest.ProductRequestStatus.IN_PROGRESS.getCode())
                .next();
    }

    public void complete(long productRequestId, Account account, DebitCard card) {
        gd.g.
                V(productRequestId).as("productRequest")
                .property("status", ProductRequest.ProductRequestStatus.COMPLETED.getCode())
                .match(
                        as("productRequest").out("ASSOCIATED_WITH").as("product"),
                        as("productRequest").inE("MAKE_REQUEST").as("makeRequest"),
                        as("makeRequest").outV().as("individual")
                )
                .addV("Account")
                    .property("number", account.getNumber().toString())
                    .property("balance", account.getBalance())
                    .property("isActive", account.isActive())
                .as("account")
                .addV("DebitCard")
                    .property("number", card.getNumber().toString())
                    .property("holderName", card.getHolderName())
                    .property("expirationDate", card.getExpirationDate())
                    .property("cvv", card.getCvv())
                    .property("isActive", card.isActive())
                .as("card")
                .addE("MANAGES").from("card").to("account")
                    .property("createdAt", new Date())
                .addE("OWNS").from("individual").to("card")
                    .property("createdAt", new Date())
                .addE("ASSOCIATED_WITH").from("card").to("product")
                .iterate();
    }
}
