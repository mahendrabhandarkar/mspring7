package com.ks.mspring7.repo;

import com.ks.mspring7.entity.OrderInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderInvoice,Long> {}
