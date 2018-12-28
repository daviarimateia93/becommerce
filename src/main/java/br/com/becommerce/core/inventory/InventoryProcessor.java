package br.com.becommerce.core.inventory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.becommerce.core.product.Product;
import br.com.becommerce.core.product.ProductService;

@Component
public class InventoryProcessor {

    private InventoryService inventoryService;
    private ProductService productService;

    public InventoryProcessor(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @Transactional
    public List<InventoryItemDTO> findItems(int page, int size) {
        return inventoryService.findItemsByIventoryId(DefaultInventory.ID, page, size)
                .stream()
                .map(this::createInventoryItemDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryItemDTO getItem(String inventoryItemId) {
        return createInventoryItemDTO(inventoryService.getItem(inventoryItemId));
    }

    @Transactional
    public InventoryItemDTO addItem(InventoryItemDTO dto) {
        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName(dto.getProductName());
        product.setDescription(dto.getProductDescription());

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setId(UUID.randomUUID().toString());
        inventoryItem.setInventoryId(DefaultInventory.ID);
        inventoryItem.setAmount(dto.getAmount());
        inventoryItem.setProductId(product.getId());

        productService.save(product);
        inventoryService.saveItem(inventoryItem);

        dto.setProductId(product.getId());
        dto.setId(inventoryItem.getId());
        dto.setInventoryId(inventoryItem.getInventoryId());

        return dto;
    }

    @Transactional
    public void removeItem(String inventoryItemId) {
        InventoryItem inventoryItem = inventoryService.getItem(inventoryItemId);

        inventoryService.deleteItem(inventoryItemId);
        productService.delete(inventoryItem.getProductId());
    }

    @Transactional
    public InventoryItemDTO updateAmount(InventoryOperationDTO dto) {
        InventoryItem inventoryItem = inventoryService.getItem(dto.getInventoryItemId());

        switch (dto.getOperation()) {
        case ADD:
            inventoryItem.addAmount(dto.getValue());
            break;
        case SUBTRACT:
            inventoryItem.subtractAmount(dto.getValue());
            break;
        }

        return createInventoryItemDTO(inventoryService.saveItem(inventoryItem));
    }

    private InventoryItemDTO createInventoryItemDTO(InventoryItem inventoryItem) {
        Product product = productService.get(inventoryItem.getProductId());

        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setId(inventoryItem.getId());
        inventoryItemDTO.setInventoryId(inventoryItem.getInventoryId());
        inventoryItemDTO.setAmount(inventoryItem.getAmount());
        inventoryItemDTO.setProductId(inventoryItem.getProductId());
        inventoryItemDTO.setProductName(product.getName());
        inventoryItemDTO.setProductDescription(product.getDescription());

        return inventoryItemDTO;
    }
}
