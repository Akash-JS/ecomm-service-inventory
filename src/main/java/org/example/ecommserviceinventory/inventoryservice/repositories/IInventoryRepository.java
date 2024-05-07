package org.example.ecommserviceinventory.inventoryservice.repositories;

import org.example.ecommserviceinventory.inventoryservice.models.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IInventoryRepository extends JpaRepository<InventoryItem,String> {
    List<InventoryItem> findByProductId(String productId);
}
