package com.example.project02.repository;

import com.example.project02.dto.OrderResponse;
import com.example.project02.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long user_id);

    Order findOrderById(Long order_id);

    @Query("select new com.example.project02.dto.OrderResponse(o.orderDate, o.orderStatus, op.product.name, op.product.price, op.count, op.price) " +
            "from Order o " +
            "join o.orderProducts op " +
            "where o.user.id = :userId")
    List<OrderResponse> findOrderDetailByUserId(Long userId);
}
