package br.com.becommerce.core.inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryService {

    <T extends Inventory> Optional<T> find(String inventoryId);

    <T extends InventoryItem> Optional<T> findItem(String inventoryItemId);

    /*
     * Get prefix instead of find, because we do not want Optional return here :)
     */
    <T extends Inventory> T get(String inventoryId);

    <T extends InventoryItem> T getItem(String inventoryItemId);

    <T extends InventoryItem> List<T> findItemsByIventoryId(String inventoryItem, int page, int size);

    <T extends Inventory> T save(T inventory);

    /*
     * I wont use method overloading here , lets keep the pattern from findItem
     */
    <T extends InventoryItem> T saveItem(T inventoryItem);

    void deleteItem(String inventoryItemId);
}
