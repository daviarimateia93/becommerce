package br.com.becommerce.infrastructure.product;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.becommerce.core.exception.NotFoundException;
import br.com.becommerce.core.product.Product;
import br.com.becommerce.core.product.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Product> Optional<T> find(String productId) {
        return (Optional<T>) productRepository.findByIdAndDeletedDateTimeIsNull(productId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Product> T get(String productId) {
        return (T) find(productId).orElseThrow(() -> new NotFoundException("Product with id %s not found", productId));
    }

    @Override
    public <T extends Product> T save(T product) {
        String id = product.getOptionalId().orElseGet(() -> UUID.randomUUID().toString());

        product.setId(id);

        return productRepository.save(product);
    }

    @Override
    public void delete(String productId) {
        find(productId).ifPresent(p -> {
            p.setDeletedDateTime(ZonedDateTime.now());

            save(p);
        });
    }
}
