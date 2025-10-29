package com.example.saratogapizza.services;

import com.example.saratogapizza.configs.RabbitConfig;
import com.example.saratogapizza.requests.InventoryMessageRequest;
import com.example.saratogapizza.responses.InventoryMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryMessageListener {

    private final InventoryService inventoryService;

    @RabbitListener(queues = RabbitConfig.INVENTORY_QUEUE)
    public InventoryMessageResponse handleInventoryMessage(InventoryMessageRequest message) {
        try {
            switch (message.getAction()) {
                case "RESERVE" -> {
                    inventoryService.addBasketUpdate(message.getProductSizeId(), message.getQuantity());
                    return new InventoryMessageResponse(true, "Stock reserved successfully");
                }
                case "RELEASE" -> {
                    inventoryService.removeFromBasketUpdate(message.getProductSizeId(), message.getQuantity());
                    return new InventoryMessageResponse(true, "Reserved stock released");
                }
                case "CONFIRM" -> {
                    inventoryService.confirmOrderStockUpdate(message.getProductSizeId(), message.getQuantity());
                    return new InventoryMessageResponse(true, "Stock confirmed and updated");
                }
                default -> {
                    return new InventoryMessageResponse(false, "Invalid action: " + message.getAction());
                }
            }
        } catch (Exception e) {
            return new InventoryMessageResponse(false, e.getMessage());
        }
    }
}
