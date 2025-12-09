package com.mayik.inventory_ms.repository;

import com.mayik.inventory_ms.repository.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
