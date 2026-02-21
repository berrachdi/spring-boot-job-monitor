package io.github.berrachdi.springbootjobmonitor.exchange.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable record of a completed trade between a buy and a sell order.
 */
@Entity
@Table(name = "trades")
@Getter
public class Trade {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyPair pair;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false)
    private UUID buyOrderId;

    @Column(nullable = false)
    private UUID sellOrderId;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    /** Required by JPA. */
    private Trade() {}

    @Builder
    private Trade(CurrencyPair pair, BigDecimal price, BigDecimal quantity,
                  UUID buyOrderId, UUID sellOrderId) {
        this.id = UUID.randomUUID();
        this.pair = pair;
        this.price = price;
        this.quantity = quantity;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.executedAt = LocalDateTime.now();
    }
}
