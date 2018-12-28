package br.com.becommerce.core.product;

import java.util.Optional;

public interface ProductService {

    <T extends Product> Optional<T> find(String productId);

    <T extends Product> T get(String productId);

    <T extends Product> T save(T product);

    void delete(String productId);
}
