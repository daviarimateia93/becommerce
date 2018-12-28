package br.com.becommerce.core.inventory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import com.github.javafaker.Faker;

import br.com.becommerce.core.product.Product;
import lombok.Data;

@Data
public class InventoryItem {

    private String id;
    private String inventoryId;
    private String productId;
    private BigDecimal amount;
    private ZonedDateTime deletedDateTime;

    public Optional<String> getOptionalId() {
        return Optional.ofNullable(id);
    }

    public void addAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public static InventoryItem fake() {
        return fake(Inventory.fake(), Product.fake());
    }

    public static InventoryItem fake(Inventory inventory, Product product) {
        InventoryItem fake = new InventoryItem();
        fake.id = UUID.randomUUID().toString();
        fake.inventoryId = inventory.getId();
        fake.productId = product.getId();
        fake.setAmount(new BigDecimal(Faker.instance().number().randomDigitNotZero()));

        return fake;
    }
}
