package io.github.berrachdi.springbootjobmonitor.exchange.repository;

import io.github.berrachdi.springbootjobmonitor.exchange.domain.CurrencyPair;
import io.github.berrachdi.springbootjobmonitor.exchange.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

    List<Trade> findByPair(CurrencyPair pair);

    List<Trade> findByBuyOrderId(UUID buyOrderId);

    List<Trade> findBySellOrderId(UUID sellOrderId);
}
