package io.github.berrachdi.springbootjobmonitor.exchange.domain;

/**
 * Lifecycle status of an order.
 */
public enum OrderStatus {
    NEW,
    PARTIALLY_FILLED,
    FILLED,
    CANCELLED
}
