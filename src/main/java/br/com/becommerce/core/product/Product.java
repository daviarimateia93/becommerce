package br.com.becommerce.core.product;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import com.github.javafaker.Faker;

import lombok.Data;

@Data
public class Product {

    private String id;
    private String name;
    private String description;
    private ZonedDateTime deletedDateTime;

    public Optional<String> getOptionalId() {
        return Optional.ofNullable(id);
    }

    public static Product fake() {
        Product fake = new Product();
        fake.id = UUID.randomUUID().toString();
        fake.name = Faker.instance().lorem().characters(150);
        fake.description = Faker.instance().lorem().characters(300);

        return fake;
    }
}
