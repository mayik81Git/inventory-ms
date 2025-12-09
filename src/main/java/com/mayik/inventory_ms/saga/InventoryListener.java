package com.mayik.inventory_ms.saga;

import com.mayik.inventory_ms.repository.InventoryRepository;
import com.mayik.inventory_ms.repository.entity.Inventory;
import com.mayik.inventory_ms.saga.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class InventoryListener {

    private final InventoryRepository inventoryRepository;
    private final RabbitTemplate rabbitTemplate;

    public InventoryListener(InventoryRepository inventoryRepository, RabbitTemplate rabbitTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "inventory-reserve-queue")
    public void handleInventoryReserve(@Payload Order order) {
        System.out.println("Inventario: Recibido comando para reservar productos del pedido " + order.getId());

        boolean allReserved = true;
        for (String item : order.getItems()) {
            Inventory inventory = inventoryRepository.findById(item).orElse(null);
            if (inventory == null || inventory.getQuantity() <= 0) {
                allReserved = false;
                break;
            }
            inventory.setQuantity(inventory.getQuantity() - 1);
            inventoryRepository.save(inventory);
        }

        if (allReserved) {
            System.out.println("Inventario: Stock reservado. Enviando inventory.reserved. Saga ID: " + order.getStatus());
            rabbitTemplate.convertAndSend("inventory-exchange", "inventory.reserved", order);
        } else {
            System.out.println("Inventario: Fallo en la reserva. Enviando inventory.failed. Saga ID: " + order.getStatus());
            rabbitTemplate.convertAndSend("inventory-exchange", "inventory.failed", order);
        }
    }

    @RabbitListener(queues = "inventory-release-queue")
    public void handleInventoryRelease(@Payload Order order) {
        System.out.println("Inventario: Recibido comando de compensación para liberar productos del pedido " + order.getId());

        for (String item : order.getItems()) {
            Inventory inventory = inventoryRepository.findById(item).orElse(null);
            if (inventory != null) {
                inventory.setQuantity(inventory.getQuantity() + 1);
                inventoryRepository.save(inventory);
            }
        }
        System.out.println("Inventario: Stock liberado. Saga ID: " + order.getStatus());
    }
}