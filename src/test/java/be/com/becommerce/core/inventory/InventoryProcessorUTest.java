package be.com.becommerce.core.inventory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import be.com.becommerce.TestMockConfiguration;
import br.com.becommerce.Application;
import br.com.becommerce.core.inventory.DefaultInventory;
import br.com.becommerce.core.inventory.Inventory;
import br.com.becommerce.core.inventory.InventoryItem;
import br.com.becommerce.core.inventory.InventoryItemDTO;
import br.com.becommerce.core.inventory.InventoryOperationDTO;
import br.com.becommerce.core.inventory.InventoryOperationDTO.Operation;
import br.com.becommerce.core.inventory.InventoryProcessor;
import br.com.becommerce.core.inventory.InventoryService;
import br.com.becommerce.core.product.Product;
import br.com.becommerce.core.product.ProductService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Import(TestMockConfiguration.class)
public class InventoryProcessorUTest {

    @Autowired
    private InventoryProcessor inventoryProcessor;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductService productService;

    private Inventory inventory;

    private List<Product> products;

    private List<InventoryItem> inventoryItems;

    private List<InventoryItemDTO> createdDTOs;

    @Before
    public void setup() {
        inventory = createInventory();

        products = createProducts();

        inventoryItems = products.stream()
                .map(p -> createInventoryItem(inventory, p))
                .collect(Collectors.toList());

        createdDTOs = inventoryItems.stream()
                .map(ii -> createInventoryItemDTO(ii,
                        products.stream().filter(p -> p.getId().equals(ii.getProductId())).findFirst().orElse(null)))
                .collect(Collectors.toList());

        when(inventoryService.findItemsByIventoryId(eq(inventory.getId()), anyInt(), anyInt()))
                .thenReturn(inventoryItems);

        when(inventoryService.saveItem(any())).thenAnswer(new Answer<InventoryItem>() {
            @Override
            public InventoryItem answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (InventoryItem) args[0];
            }
        });

        inventoryItems.forEach(ii -> when(inventoryService.getItem(eq(ii.getId()))).thenReturn(ii));

        products.forEach(p -> when(productService.get(eq(p.getId()))).thenReturn(p));

    }

    @Test
    public void findItems() {
        List<InventoryItemDTO> items = inventoryProcessor.findItems(0, 15);

        assertThat("returned items is not null", items, not(nullValue()));
        assertThat("returned items is equals to created ones", items,
                hasItems(createdDTOs.toArray(new InventoryItemDTO[] {})));
    }

    @Test
    public void findItem() {
        InventoryItemDTO created = createdDTOs.get(0);
        InventoryItemDTO item = inventoryProcessor.getItem(created.getId());

        assertThat("returned item is not null", item, not(nullValue()));
        assertThat("returned item is equals to created one", item, is(created));
    }

    @Test
    public void addItem() {
        InventoryItemDTO created = createdDTOs.get(0);
        created.setId(null);
        created.setInventoryId(null);
        created.setProductId(null);

        InventoryItemDTO item = inventoryProcessor.addItem(created);

        assertThat("returned item is not null", item, not(nullValue()));
        assertThat("returned item has id", item.getId(), not(nullValue()));
        assertThat("returned item has inventoryId", item.getInventoryId(), not(nullValue()));
        assertThat("returned item has productId", item.getProductId(), not(nullValue()));
        assertThat("returned item amount is the same", item.getAmount(), is(created.getAmount()));
        assertThat("returned item product name is the same", item.getProductName(), is(created.getProductName()));
        assertThat("returned item product description is the same", item.getProductDescription(),
                is(created.getProductDescription()));
    }

    @Test
    public void removeItem() {
        InventoryItemDTO created = createdDTOs.get(0);

        inventoryProcessor.removeItem(created.getId());

        // There is nothing to **UNIT** assert here
        // Our remove design is void without logic
        // We only set deletedDateTime property
    }

    @Test
    public void updateAmountAdding() {
        BigDecimal value = new BigDecimal("0.5");

        InventoryItemDTO created = createdDTOs.get(0);

        InventoryOperationDTO operation = new InventoryOperationDTO();
        operation.setInventoryItemId(created.getId());
        operation.setOperation(Operation.ADD);
        operation.setValue(value);

        InventoryItemDTO item = inventoryProcessor.updateAmount(operation);

        assertThat("returned item is not null", item, not(nullValue()));
        assertThat("returned item has id", item.getId(), not(nullValue()));
        assertThat("returned item has inventoryId", item.getInventoryId(), not(nullValue()));
        assertThat("returned item has productId", item.getProductId(), not(nullValue()));
        assertThat("returned item amount is the same", item.getAmount(), is(created.getAmount().add(value)));
        assertThat("returned item product name is the same", item.getProductName(), is(created.getProductName()));
        assertThat("returned item product description is the same", item.getProductDescription(),
                is(created.getProductDescription()));
    }

    @Test
    public void updateAmountSubtracting() {
        BigDecimal value = new BigDecimal("0.5");

        InventoryItemDTO created = createdDTOs.get(0);

        InventoryOperationDTO operation = new InventoryOperationDTO();
        operation.setInventoryItemId(created.getId());
        operation.setOperation(Operation.SUBTRACT);
        operation.setValue(value);

        InventoryItemDTO item = inventoryProcessor.updateAmount(operation);

        assertThat("returned item is not null", item, not(nullValue()));
        assertThat("returned item has id", item.getId(), not(nullValue()));
        assertThat("returned item has inventoryId", item.getInventoryId(), not(nullValue()));
        assertThat("returned item has productId", item.getProductId(), not(nullValue()));
        assertThat("returned item amount is the same", item.getAmount(), is(created.getAmount().subtract(value)));
        assertThat("returned item product name is the same", item.getProductName(), is(created.getProductName()));
        assertThat("returned item product description is the same", item.getProductDescription(),
                is(created.getProductDescription()));
    }

    private InventoryItemDTO createInventoryItemDTO(InventoryItem inventoryItem, Product product) {
        InventoryItemDTO inventoryItemDTO = new InventoryItemDTO();
        inventoryItemDTO.setId(inventoryItem.getId());
        inventoryItemDTO.setInventoryId(inventoryItem.getInventoryId());
        inventoryItemDTO.setAmount(inventoryItem.getAmount());
        inventoryItemDTO.setProductId(inventoryItem.getProductId());
        inventoryItemDTO.setProductName(product.getName());
        inventoryItemDTO.setProductDescription(product.getDescription());

        return inventoryItemDTO;
    }

    private List<Product> createProducts() {
        return IntStream.range(0, 15)
                .mapToObj(i -> Product.fake())
                .collect(Collectors.toList());
    }

    private Inventory createInventory() {
        return new DefaultInventory();
    }

    private InventoryItem createInventoryItem(Inventory inventory, Product product) {
        return InventoryItem.fake(inventory, product);
    }
}
