package br.com.becommerce.infrastructure.inventory;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.becommerce.core.inventory.InventoryItemDTO;
import br.com.becommerce.core.inventory.InventoryOperationDTO;
import br.com.becommerce.core.inventory.InventoryProcessor;

@RestController
@RequestMapping("/inventories/default/items")
public class InventoryController {

    private InventoryProcessor inventoryProcessor;

    public InventoryController(InventoryProcessor inventoryProcessor) {
        this.inventoryProcessor = inventoryProcessor;
    }

    @GetMapping
    public List<InventoryItemDTO> findItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        return inventoryProcessor.findItems(page, size);
    }

    @GetMapping("/{inventoryItemId}")
    public InventoryItemDTO getItem(@PathVariable String inventoryItemId) {
        return inventoryProcessor.getItem(inventoryItemId);
    }

    @PostMapping
    public InventoryItemDTO addItem(@Valid @RequestBody InventoryItemDTO dto) {
        return inventoryProcessor.addItem(dto);
    }

    @DeleteMapping("/{inventoryItemId}")
    public void removeItem(@PathVariable String inventoryItemId) {
        inventoryProcessor.removeItem(inventoryItemId);
    }

    @PutMapping("/{inventoryItemId}/amount")
    public InventoryItemDTO updateAmount(
            @PathVariable String inventoryItemId,
            @Valid @RequestBody InventoryOperationDTO dto) {

        dto.setInventoryItemId(inventoryItemId);

        return inventoryProcessor.updateAmount(dto);
    }
}
