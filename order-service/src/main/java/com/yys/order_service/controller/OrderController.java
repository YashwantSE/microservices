package com.yys.order_service.controller;

import com.yys.order_service.dto.OrderRequest;
import com.yys.order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    @PostMapping
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "fallBackMethod")  // or name as per application property
    @TimeLimiter(name = "inventory-service")    //  after using time limiter use Completable future
    @Retry(name = "inventory-service")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest){
      return CompletableFuture.supplyAsync(()->orderService.placeOrder(orderRequest) ) ;
    }

    // fallback method if something is wrong
    public CompletableFuture<String> fallBackMethod(OrderRequest request, RuntimeException runtimeException){
       return  CompletableFuture.supplyAsync(()-> "OOPS! order after sometime") ;
    }
}
