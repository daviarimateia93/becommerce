package br.com.becommerce.infrastructure.product;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.becommerce.core.product.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {

    <T extends Product> Optional<T> findByIdAndDeletedDateTimeIsNull(String id);
}
