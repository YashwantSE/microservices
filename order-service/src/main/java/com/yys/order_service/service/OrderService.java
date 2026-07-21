package com.yys.order_service.service;

import com.yys.order_service.dto.OrderLineItemDto;
import com.yys.order_service.dto.OrderRequest;
import com.yys.order_service.model.Order;
import com.yys.order_service.model.OrderLineItem;
import com.yys.order_service.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItem = orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::maptoDto).toList();

        order.setOrderLineItemList(orderLineItem);
        orderRepository.save(order);
    }

    private OrderLineItem maptoDto(OrderLineItemDto orderLineItemDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        return  orderLineItem;
    }
}
