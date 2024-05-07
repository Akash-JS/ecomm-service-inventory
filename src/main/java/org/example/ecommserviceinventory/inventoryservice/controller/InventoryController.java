package org.example.ecommserviceinventory.inventoryservice.controller;

import org.example.ecommserviceinventory.inventoryservice.models.InventoryItem;
import org.example.ecommserviceinventory.inventoryservice.repositories.IInventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/inventory-item")
public class InventoryController {
    private IInventoryRepository inventoryRepository;

    public InventoryController(IInventoryRepository inventoryRepository)
    {
        this.inventoryRepository = inventoryRepository;
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

    @PostMapping
    public ResponseEntity<String> createInventoryItem(@RequestBody InventoryItem inventoryItem)
    {
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
}
