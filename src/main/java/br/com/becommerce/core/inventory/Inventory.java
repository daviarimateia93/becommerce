package br.com.becommerce.core.inventory;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import com.github.javafaker.Faker;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.PROTECTED)
public class Inventory {

    private String id;
    private String name;
    private ZonedDateTime deletedDateTime;

    public Optional<String> getOptionalId() {
        return Optional.ofNullable(id);
    }

    public static Inventory fake() {
        Inventory fake = new Inventory();
        fake.id = UUID.randomUUID().toString();
        fake.name = Faker.instance().lorem().characters();

        return fake;
    }
}
