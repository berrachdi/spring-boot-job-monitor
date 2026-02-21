package io.github.berrachdi.springbootjobmonitor.exchange.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a trading order in the exchange engine.
 * The {@link #fill(BigDecimal)} method is thread-safe via atomic CAS operations.
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyPair pair;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side side;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal price;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // AtomicReference allows CAS-based thread-safe updates to mutable state
    @Transient
    private final AtomicReference<BigDecimal> remainingQuantityRef = new AtomicReference<>();

    @Transient
    private final AtomicReference<OrderStatus> statusRef = new AtomicReference<>();

    // JPA-persisted counterparts (written on flush)
    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal remainingQuantity;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    public Order(CurrencyPair pair, Side side, BigDecimal price, BigDecimal quantity, String userId) {
        this.id = UUID.randomUUID();
        this.pair = pair;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.NEW;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.remainingQuantityRef.set(quantity);
        this.statusRef.set(OrderStatus.NEW);
    }

    @PostLoad
    private void initTransientFields() {
        remainingQuantityRef.set(remainingQuantity);
        statusRef.set(status);
    }

    /**
     * Atomically fills {@code qty} from the remaining quantity of this order.
     * Uses a CAS loop to ensure thread-safety without external locking.
     *
     * @param qty the quantity to fill — must be positive and ≤ remaining quantity
     * @throws IllegalArgumentException if qty is null, non-positive, or exceeds remaining quantity
     */
    public void fill(BigDecimal qty) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fill quantity must be positive");
        }
        while (true) {
            BigDecimal current = remainingQuantityRef.get();
            if (qty.compareTo(current) > 0) {
                throw new IllegalArgumentException(
                        "Fill quantity " + qty + " exceeds remaining quantity " + current);
            }
            BigDecimal updated = current.subtract(qty);
            if (remainingQuantityRef.compareAndSet(current, updated)) {
                this.remainingQuantity = updated;
                OrderStatus newStatus = updated.compareTo(BigDecimal.ZERO) == 0
                        ? OrderStatus.FILLED
                        : OrderStatus.PARTIALLY_FILLED;
                statusRef.set(newStatus);
                this.status = newStatus;
                return;
            }
        }
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantityRef.get();
    }

    public OrderStatus getStatus() {
        return statusRef.get();
    }
}
