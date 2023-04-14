package com.serguni.services;

import com.serguni.models.Product;
import com.serguni.repositories.ProductRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.create(product);
    }

    public Stream<Product> getAll(int skip, int limit) {
        return productRepository.getAll(skip, limit);
    }

    public long incrCount(String name) {
        return productRepository.incrCount(name);
    }

    public Product getByName(String name) {
        return productRepository.getByName(name);
    }
}
