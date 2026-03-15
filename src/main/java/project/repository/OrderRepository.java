package project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
