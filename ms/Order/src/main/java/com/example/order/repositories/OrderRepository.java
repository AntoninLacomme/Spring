package com.example.order.repositories;

import com.example.order.domain.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<MyOrder, Long> {
}
