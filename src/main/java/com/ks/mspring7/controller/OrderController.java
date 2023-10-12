package com.ks.mspring7.controller;

import com.ks.mspring7.entity.OrderInvoice;
import com.ks.mspring7.repo.OrderRepository;
import com.ks.mspring7.stateenum.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

public class OrderController {

 @Autowired
 OrderRepository orderRepository;

 @PostMapping("/createOrder")
 public OrderInvoice createOrder(){
   OrderInvoice order = new OrderInvoice();
   order.setState(OrderStates.SUBMITTED.name());
   order.setLocalDate(LocalDate.now());
   return orderRepository.save(order);
 }
}