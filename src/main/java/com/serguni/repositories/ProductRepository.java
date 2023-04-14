package com.serguni.repositories;

import com.serguni.models.Product;
import com.serguni.utils.CamelCaseObjectMapperUtil;
import org.apache.tinkerpop.gremlin.structure.T;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;


@ApplicationScoped
public class ProductRepository extends AbstractRepository {

    public Product create(Product product) {

        return CamelCaseObjectMapperUtil.convertValue(
                gd.g()
                .addV(product.getClass().getSimpleName())
                    .property("name", product.getName())
                    .property("rate", product.getRate())
                    .property("description", product.getDescription())
                    .property("period", product.getPeriod())
                    .property("count", product.getCount())
                .elementMap()
                .next(), Product.class
        );
    }

    public Stream<Product> getAll(int skip, int limit) {
        return gd.g()
                .V()
                .hasLabel(Product.class.getSimpleName())
                .skip(skip)
                .limit(limit)
                .elementMap()
                .toStream()
                .map((p) -> CamelCaseObjectMapperUtil.convertValue(p, Product.class));
    }

    public long incrCount(String name) {
        return (long) gd.g().V().has(Product.class.getSimpleName(), "name", name)
                .property("count", union(values("count"), constant(1)).sum())
                .values("count")
                .next();
    }

    public Product getByName(String name) {

        var product = gd.g()
                .V()
                .has(Product.class.getSimpleName(), "name", name)
                .elementMap()
                .next();
        return CamelCaseObjectMapperUtil.convertValue(product, Product.class);
    }

}
