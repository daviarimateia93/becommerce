package br.com.becommerce.infrastructure.inventory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.becommerce.core.exception.NotFoundException;
import br.com.becommerce.core.inventory.Inventory;
import br.com.becommerce.core.inventory.InventoryItem;
import br.com.becommerce.core.inventory.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    private InventoryRepository inventoryRepository;
    private InventoryItemRepository inventoryItemRepository;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryItemRepository iventoryItemRepository) {

        this.inventoryRepository = inventoryRepository;
        this.inventoryItemRepository = iventoryItemRepository;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> Optional<T> find(String inventoryId) {
        return (Optional<T>) inventoryRepository.findByIdAndDeletedDateTimeIsNull(inventoryId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends InventoryItem> Optional<T> findItem(String inventoryItemId) {
        return (Optional<T>) inventoryItemRepository.findByIdAndDeletedDateTimeIsNull(inventoryItemId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T get(String inventoryId) {
        return (T) find(inventoryId)
                .orElseThrow(() -> new NotFoundException("Inventory with id %s not found", inventoryId));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends InventoryItem> T getItem(String inventoryItemId) {
        return (T) findItem(inventoryItemId)
                .orElseThrow(() -> new NotFoundException("InventoryItem with id %s not found", inventoryItemId));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends InventoryItem> List<T> findItemsByIventoryId(String inventoryId, int page, int size) {
        return (List<T>) inventoryItemRepository.findByInventoryIdAndDeletedDateTimeIsNull(
                inventoryId, PageRequest.of(page, size));
    }

    @Override
    public <T extends Inventory> T save(T inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public <T extends InventoryItem> T saveItem(T inventoryItem) {
        String id = inventoryItem.getOptionalId().orElseGet(() -> UUID.randomUUID().toString());

        inventoryItem.setId(id);

        return inventoryItemRepository.save(inventoryItem);
    }

    @Override
    public void deleteItem(String inventoryItemId) {
        findItem(inventoryItemId).ifPresent(ii -> {
            ii.setDeletedDateTime(ZonedDateTime.now());

            saveItem(ii);
        });
    }
}
