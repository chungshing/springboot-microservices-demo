package com.example.order_service.service;

import com.example.order_service.dto.InventoryResponse;
import com.example.order_service.dto.OrderLineItemsDto;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderLineItems;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCode = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        WebClient webClient = webClientBuilder.build();
        //System.out.println("Requesting inventory for SKU codes: " + skuCode);

        // Call Inventory Service, place order if there is stock
        InventoryResponse[] inventoryResponseArray = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("inventory-service")
                        .path("/api/inventory")
                        .queryParam("skuCode", skuCode.toArray()) // Convert List to Array for queryParam
                        .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .doOnNext(response -> System.out.println("Inventory response: " + Arrays.toString(response)))
                .block();

        System.out.println("Received inventory response: " + Arrays.toString(inventoryResponseArray));

        if (inventoryResponseArray == null || inventoryResponseArray.length == 0) {
            throw new RuntimeException("Failed to retrieve inventory data or products not available");
        }
        
        // Separate in-stock and out-of-stock items
        List<String> inStockSkuCodes = Arrays.stream(inventoryResponseArray)
                .filter(InventoryResponse::isInStock)
                .map(InventoryResponse::getSkuCode)
                .collect(Collectors.toList());

        List<String> outOfStockSkuCodes = Arrays.stream(inventoryResponseArray)
                .filter(response -> !response.isInStock())
                .map(InventoryResponse::getSkuCode)
                .collect(Collectors.toList());

        if (!inStockSkuCodes.isEmpty()) {
            // Filter order line items to only include those that are in stock
            List<OrderLineItems> inStockOrderLineItems = order.getOrderLineItemsList().stream()
                    .filter(item -> inStockSkuCodes.contains(item.getSkuCode()))
                    .collect(Collectors.toList());

            order.setOrderLineItemsList(inStockOrderLineItems);
            orderRepository.save(order);

            // Construct a detailed response message
            StringBuilder responseMessage = new StringBuilder("Order Placed Successfully for in-stock items: " + inStockSkuCodes);
            if (!outOfStockSkuCodes.isEmpty()) {
                responseMessage.append(". Out of stock items: ").append(outOfStockSkuCodes);
            }
            return responseMessage.toString();
        } else {
            throw new IllegalArgumentException("No products in stock");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
