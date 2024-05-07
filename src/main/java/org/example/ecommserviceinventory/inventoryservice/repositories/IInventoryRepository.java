package org.example.ecommserviceinventory.inventoryservice.repositories;

import org.example.ecommserviceinventory.inventoryservice.models.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInventoryRepository extends JpaRepository<InventoryItem,String> {
}
