package org.example.ecommserviceinventory.inventoryservice.controller;

import org.example.ecommserviceinventory.WebClientConfig;
import org.example.ecommserviceinventory.inventoryservice.models.InventoryItem;
import org.example.ecommserviceinventory.inventoryservice.models.Product;
import org.example.ecommserviceinventory.inventoryservice.repositories.IInventoryRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/inventory-item")
public class InventoryController {
    private IInventoryRepository inventoryRepository;
    private WebClient webClient;

    public InventoryController(IInventoryRepository inventoryRepository, WebClient webClient)
    {
        this.inventoryRepository = inventoryRepository;
        this.webClient = webClient;

    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getInventoryItems()
    {
        List<InventoryItem> inventoryItems = inventoryRepository.findAll();
        return ResponseEntity.ok().body(inventoryItems);
    }

    @GetMapping("{inventoryitemId}")
    public ResponseEntity<Optional<InventoryItem>> getInventoryItemById(@PathVariable String inventoryitemId)
    {
        Optional<InventoryItem> inventoryItem = inventoryRepository.findById(inventoryitemId);
        return ResponseEntity.ok().body(inventoryItem);
    }

    @GetMapping("product/{productId}")
    public ResponseEntity<Optional<InventoryItem>> getInventoryItemByProductId(@PathVariable String productId)
    {
        Optional<InventoryItem> inventoryItem = inventoryRepository.findByProductId(productId).stream().findFirst();
        return ResponseEntity.ok().body(inventoryItem);
    }

    @PostMapping
    public ResponseEntity<String> createInventoryItem(@RequestBody InventoryItem inventoryItem)
    {
        if(inventoryItem == null)
        {
            return ResponseEntity.badRequest().body("Inventory item cannot be null");
        }

        if(!isProductExist(inventoryItem.getProductId()))
        {
            return ResponseEntity.badRequest().body("Product does not exist");
        }
        // Check for the product existance;
        inventoryItem.setId(UUID.randomUUID().toString());
        inventoryRepository.save(inventoryItem);
        return ResponseEntity.ok().body("Inventory item created with id " + inventoryItem.getId());
    }

    @PostMapping("{inventoryitemId}/add-quantity/{quantity}")
    public  ResponseEntity<String> addInventoryQuantity(@PathVariable String inventoryitemId, @PathVariable int quantity)
    {
        Optional<InventoryItem> item = inventoryRepository.findById(inventoryitemId);

        if(item.isEmpty())
        {
            return  ResponseEntity.badRequest().build();
        }

        item.get().setQuantity(item.get().getQuantity() + quantity);
        inventoryRepository.save(item.get());
        return ResponseEntity.ok().body("Inventory item added with id " + inventoryitemId);
    }

    @PostMapping("{inventoryitemId}/remove-quantity/{quantity}")
    public  ResponseEntity<String> removeInventoryQuantity(@PathVariable String inventoryitemId, @PathVariable int quantity)
    {
        Optional<InventoryItem> item = inventoryRepository.findById(inventoryitemId);

        if(item.isEmpty())
        {
            return  ResponseEntity.badRequest().build();
        }

        if(item.get().getQuantity() < quantity)
        {
            return  ResponseEntity.badRequest().body("Quantity to remove is less than the given quantity");
        }

        int finalQuantity = item.get().getQuantity() - quantity;

        if(finalQuantity == 0)
        {
            inventoryRepository.deleteById(inventoryitemId);
            return ResponseEntity.ok().body("Inventory item removed as 0 quantity left for " + inventoryitemId);
        }

        item.get().setQuantity(finalQuantity);
        inventoryRepository.save(item.get());
        return ResponseEntity.ok().body("Inventory item quantity removed with id " + inventoryitemId);
    }

    private boolean isProductExist(String productId)
    {

        Product product = webClient.get()
                .uri("ecomm-service-product/api/v1/products/"+productId)
                .retrieve().bodyToMono(Product.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatusCode.valueOf(404)) {
                        // Handle the "not found" case here
                        // For example, return a default MyModel or throw an exception
                        return Mono.empty(); // Return an empty Mono
                    } else {
                        // For other errors, rethrow the exception
                        return Mono.error(ex);
                    }
                })
                .block();


        return  product != null;
    }
}
