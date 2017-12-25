package com.orders.repos;

import com.orders.models.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    public Order findById(long id);
    public List<Order> findByUserId(long userId);
    public List<Order> findAll(Pageable pageable);
}
