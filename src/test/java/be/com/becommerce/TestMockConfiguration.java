package be.com.becommerce;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import br.com.becommerce.core.inventory.InventoryService;
import br.com.becommerce.core.product.ProductService;

@Profile("test")
@Configuration
public class TestMockConfiguration {

    @Bean
    @Primary
    public InventoryService inventoryService() {
        return Mockito.mock(InventoryService.class);
    }

    @Bean
    @Primary
    public ProductService productService() {
        return Mockito.mock(ProductService.class);
    }
}
