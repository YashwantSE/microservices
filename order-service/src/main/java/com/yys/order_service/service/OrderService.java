package com.yys.order_service.service;

import com.yys.order_service.Config.WebClientConfig;
import com.yys.order_service.dto.InventoryResponse;
import com.yys.order_service.dto.OrderLineItemDto;
import com.yys.order_service.dto.OrderRequest;
import com.yys.order_service.model.Order;
import com.yys.order_service.model.OrderLineItem;
import com.yys.order_service.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItem = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::maptoDto).toList();

        order.setOrderLineItemList(orderLineItem);

        List<String> skuCodes =  order.getOrderLineItemList().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();


        // call inventory service and place order if product is in stock
       InventoryResponse[] inventoryResponseArray = webClient.get().uri("http://localhost:8082/api/inventory",
                       uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve()
                         .bodyToMono(InventoryResponse[].class)  // response body is boolean
                        .block();

       boolean allProductInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
       if(allProductInStock) {
           orderRepository.save(order);
       }else{
           throw new IllegalArgumentException("Product is not in stock, please try again later");
       }
    }

    private OrderLineItem maptoDto(OrderLineItemDto orderLineItemDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return  orderLineItem;
    }
}
