package br.com.becommerce.infrastructure.inventory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.becommerce.core.inventory.InventoryItem;

@Repository
public interface InventoryItemRepository extends CrudRepository<InventoryItem, String> {

    <T extends InventoryItem> Optional<T> findByIdAndDeletedDateTimeIsNull(String id);

    <T extends InventoryItem> List<T> findByInventoryIdAndDeletedDateTimeIsNull(
            String inventoryId, Pageable pageable);
}
