package com.mayik.inventory_ms.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Entity
@Data
public class Inventory implements Serializable {
    @Id
    private String productId;
    private int quantity;
}
