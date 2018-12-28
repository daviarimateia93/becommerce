package br.com.becommerce.infrastructure.inventory;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.becommerce.core.inventory.Inventory;

@Repository
public interface InventoryRepository extends CrudRepository<Inventory, String> {

    <T extends Inventory> Optional<T> findByIdAndDeletedDateTimeIsNull(String id);
}
