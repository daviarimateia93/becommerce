package be.com.becommerce.core.inventory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.becommerce.Application;
import br.com.becommerce.core.inventory.DefaultInventory;
import br.com.becommerce.core.inventory.Inventory;
import br.com.becommerce.core.inventory.InventoryItem;
import br.com.becommerce.core.inventory.InventoryItemDTO;
import br.com.becommerce.core.inventory.InventoryOperationDTO;
import br.com.becommerce.core.inventory.InventoryOperationDTO.Operation;
import br.com.becommerce.core.product.Product;

/*
 * I've choose to use restTemplate instead of mockMvc... because I really want integration tests
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class InventoryProcessorITest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CrudRepository<Product, String> productRepository;

    @Autowired
    private CrudRepository<Inventory, String> inventoryRepository;

    @Autowired
    private CrudRepository<InventoryItem, String> inventoryItemRepository;

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

        products.forEach(p -> productRepository.save(p));
        inventoryRepository.save(inventory);
        inventoryItems.forEach(ii -> inventoryItemRepository.save(ii));
    }

    @Test
    public void testItems() {
        ResponseEntity<InventoryItemDTO[]> response = restTemplate.getForEntity(
                createUrl("/inventories/default/items"), InventoryItemDTO[].class);

        List<InventoryItemDTO> items = Arrays.asList(response.getBody());

        InventoryItem[] foundItems = toArray(inventoryItemRepository.findAllById(
                createdDTOs.stream().map(InventoryItemDTO::getId).collect(Collectors.toList())), InventoryItem.class);

        Product[] foundProducts = toArray(productRepository.findAllById(
                createdDTOs.stream().map(InventoryItemDTO::getId).collect(Collectors.toList())), Product.class);

        assertThat("returned status code is 200", response.getStatusCode(), is(HttpStatus.OK));
        assertThat("returned items is equals to created ones", items,
                hasItems(createdDTOs.toArray(new InventoryItemDTO[] {})));
        assertThat("created item is persisted", inventoryItems, hasItems(foundItems));
        assertThat("created product is persisted", products, hasItems(foundProducts));
    }

    @Test
    public void findItem() {
        InventoryItemDTO created = createdDTOs.get(0);

        ResponseEntity<InventoryItemDTO> response = restTemplate.getForEntity(
                createUrl("/inventories/default/items/%s", created.getId()), InventoryItemDTO.class);

        InventoryItemDTO item = response.getBody();

        InventoryItem foundItem = inventoryItemRepository.findById(created.getId()).orElse(null);

        Product foundProduct = productRepository.findById(created.getProductId()).orElse(null);

        assertThat("returned status code is 200", response.getStatusCode(), is(HttpStatus.OK));
        assertThat("returned item is not null", item, not(nullValue()));
        assertThat("returned item is equals to created one", item, is(created));
        assertThat("created item is persisted", inventoryItems.get(0), is(foundItem));
        assertThat("created product is persisted", products.get(0), is(foundProduct));
    }

    @Test
    public void addItem() {
        InventoryItemDTO created = createdDTOs.get(0);
        created.setId(null);
        created.setInventoryId(null);
        created.setProductId(null);

        ResponseEntity<InventoryItemDTO> response = restTemplate.postForEntity(
                createUrl("/inventories/default/items"), created, InventoryItemDTO.class);

        InventoryItemDTO item = response.getBody();

        assertThat("returned status code is 200", response.getStatusCode(), is(HttpStatus.OK));
        assertThat("returned item is not null", item, not(nullValue()));
        assertThat("returned item has id", item.getId(), not(nullValue()));
        assertThat("returned item has inventoryId", item.getInventoryId(), not(nullValue()));
        assertThat("returned item has productId", item.getProductId(), not(nullValue()));

        InventoryItem createdItem = inventoryItems.get(0);
        createdItem.setId(item.getId());
        createdItem.setInventoryId(item.getInventoryId());
        createdItem.setProductId(item.getProductId());

        Product createdProduct = products.get(0);
        createdProduct.setId(item.getProductId());

        InventoryItem foundItem = inventoryItemRepository.findById(item.getId()).orElse(null);

        Product foundProduct = productRepository.findById(item.getProductId()).orElse(null);

        assertThat("returned item amount is the same", item.getAmount(), is(created.getAmount()));
        assertThat("returned item product name is the same", item.getProductName(), is(created.getProductName()));
        assertThat("returned item product description is the same", item.getProductDescription(),
                is(created.getProductDescription()));
        assertThat("created item is persisted", createdItem, is(foundItem));
        assertThat("created product is persisted", createdProduct, is(foundProduct));
    }

    @Test
    public void removeItem() {
        InventoryItemDTO created = createdDTOs.get(0);

        ResponseEntity<Void> response = restTemplate.exchange(
                createUrl("/inventories/default/items/%s", created.getId()),
                HttpMethod.DELETE,
                null,
                Void.class);

        InventoryItem foundItem = inventoryItemRepository.findById(created.getId()).orElse(null);
        Product foundProduct = productRepository.findById(created.getProductId()).orElse(null);

        assertThat("returned status code is 200", response.getStatusCode(), is(HttpStatus.OK));
        assertThat("item was deleted", foundItem.getDeletedDateTime(), not(nullValue()));
        assertThat("product was deleted", foundProduct.getDeletedDateTime(), not(nullValue()));
    }

    @Test
    public void updateAmountAdding() {
        BigDecimal value = new BigDecimal("0.5");

        InventoryItemDTO created = createdDTOs.get(0);

        InventoryOperationDTO operation = new InventoryOperationDTO();
        operation.setOperation(Operation.ADD);
        operation.setValue(value);

        ResponseEntity<InventoryItemDTO> response = restTemplate.exchange(
                createUrl("/inventories/default/items/%s/amount", created.getId()),
                HttpMethod.PUT,
                new HttpEntity<InventoryOperationDTO>(operation),
                InventoryItemDTO.class);

        InventoryItemDTO item = response.getBody();

        assertThat("returned status code is 200", response.getStatusCode(), is(HttpStatus.OK));
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
        operation.setOperation(Operation.SUBTRACT);
        operation.setValue(value);

        ResponseEntity<InventoryItemDTO> response = restTemplate.exchange(
                createUrl("/inventories/default/items/%s/amount", created.getId()),
                HttpMethod.PUT,
                new HttpEntity<InventoryOperationDTO>(operation),
                InventoryItemDTO.class);

        InventoryItemDTO item = response.getBody();

        assertThat("returned status code is 200", response.getStatusCode(), is(HttpStatus.OK));
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

    private String getUrl() {
        return String.format("http://localhost:%s", port);
    }

    private String createUrl(String format, Object... objects) {
        return String.format(getUrl() + format, objects);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] toArray(Iterable<T> iterable, Class<T> type) {
        List<T> list = new ArrayList<>();
        iterable.iterator().forEachRemaining(list::add);

        return list.toArray((T[]) Array.newInstance(type, list.size()));
    }
}
