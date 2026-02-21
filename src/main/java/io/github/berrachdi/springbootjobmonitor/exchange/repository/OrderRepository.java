package io.github.berrachdi.springbootjobmonitor.exchange.repository;

import io.github.berrachdi.springbootjobmonitor.exchange.domain.Order;
import io.github.berrachdi.springbootjobmonitor.exchange.domain.CurrencyPair;
import io.github.berrachdi.springbootjobmonitor.exchange.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByPairAndStatus(CurrencyPair pair, OrderStatus status);

    List<Order> findByUserId(String userId);
}
