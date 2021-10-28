package com.thanglv.batch.repository;

import com.thanglv.batch.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author acer on 10/28/2021
 * @project spring-batch-demo
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
