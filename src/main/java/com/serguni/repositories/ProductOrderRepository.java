package com.serguni.repositories;

import com.serguni.models.Account;
import com.serguni.models.DebitCard;
import com.serguni.models.ProductOrder;
import com.serguni.utils.CamelCaseObjectMapperUtil;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.Order.desc;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.as;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;


@ApplicationScoped
public class ProductOrderRepository extends  AbstractRepository {

    public long create(ProductOrder productOrder) {
        return (long) gd.g
                .V()
                    .hasLabel("Individual")
                    .has("sub", productOrder.getUserSub())
                    .as("individual")
                .V()
                    .hasLabel("Product")
                    .has("name", productOrder.getProductName())
                .as("product")
                .addV(ProductOrder.class.getSimpleName())
                    .property("address", productOrder.getAddress())
                    .property("scheduledDate", productOrder.getScheduledDate())
                    .property("status", productOrder.getStatus().getCode())
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

    public Stream<ProductOrder> getAllByStatus(ProductOrder.ProductOrderStatus status, int skip, int limit) {

        var traverse = gd.g.V()
                .match(
                        as("productOrder").has(ProductOrder.class.getSimpleName(), "status", status.getCode()),
                        as("productOrder").inE("MAKE_REQUEST").as("makeRequest"),
                        as("makeRequest").outV().as("individual"),
                        as("productOrder").out("ASSOCIATED_WITH").as("product")
                );

        return makeProjection(traverse)
                .skip(skip)
                .limit(limit)
                .toStream()
                .map((pr) -> CamelCaseObjectMapperUtil.convertValue(pr, ProductOrder.class));

    }

    public Stream<ProductOrder> getAllByUserSub(String userSub, int skip, int limit) {

        var traverse = gd.g.V()
                .match(
                        as("individual").has("Individual","sub", userSub),
                        as("individual").outE("MAKE_REQUEST").as("makeRequest"),
                        as("makeRequest").inV().as("productOrder"),
                        as("productOrder").out("ASSOCIATED_WITH").as("product")
                )
                .order()
                .by(select("makeRequest").values("createdAt"), desc);

        return makeProjection(traverse)
                .skip(skip)
                .limit(limit)
                .toStream()
                .map((pr) -> CamelCaseObjectMapperUtil.convertValue(pr, ProductOrder.class));
    }

    private GraphTraversal<Vertex, Map<String, Object>> makeProjection(GraphTraversal<Vertex, Map<String, Object>> graphTraversal) {
        return graphTraversal
                .project("id","userSub","createdAt", "productName", "address", "scheduledDate", "status")
                .by(select("productOrder").values(T.id.getAccessor()))
                .by(select("individual").values("sub"))
                .by(select("makeRequest").values("createdAt"))
                .by(select("product").values("name"))
                .by(select("productOrder").values("address"))
                .by(select("productOrder").values("scheduledDate"))
                .by(select("productOrder").values("status"));
    }

    public ProductOrder getById(long id) {
        var traverse = gd.g
                .V(id).as("productOrder")
                .match(
                    as("productOrder").inE("MAKE_REQUEST").as("makeRequest"),
                    as("makeRequest").outV().as("individual"),
                    as("productOrder").out("ASSOCIATED_WITH").as("product")
                );
        return CamelCaseObjectMapperUtil.convertValue(makeProjection(traverse).next(), ProductOrder.class);
    }

    public void confirm(long productOrderId) {
        // TODO: ПРОВЕРИТЬ ТЕКУЩИЙ СТАТУС
        gd.g.V(productOrderId)
                .property("status", ProductOrder.ProductOrderStatus.IN_PROGRESS.getCode())
                .next();
    }

    public void refuse(long productOrderId) {
        // TODO: ПРОВЕРИТЬ ТЕКУЩИЙ СТАТУС
        gd.g.V(productOrderId)
                .property("status", ProductOrder.ProductOrderStatus.REFUSED.getCode())
                .outE("RESERVES").as("reserves")
                .inV().as("card")
                .V().hasLabel("Free").as("free")
                .addE("IN")
                .from("card")
                .to("free")
                .property("createdAt", new Date())
                .select("reserves").drop()
                .iterate();
    }

    public void complete(long productOrderId) {
        gd.g
                .V(productOrderId).as("productOrder")
                .property("status", ProductOrder.ProductOrderStatus.COMPLETED.getCode())
                .match(
                        as("productOrder").outE("RESERVES").as("reserves"),
                        as("reserves").inV().as("card"),
                        as("productOrder").in("MAKE_REQUEST").as("individual")
                )
                .addE("OWNS")
                .from("individual")
                .to("card")
                .select("reserves").drop()
                .iterate();
    }
}
