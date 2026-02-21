package io.github.berrachdi.springbootjobmonitor.exchange;

import io.github.berrachdi.springbootjobmonitor.exchange.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Order#fill(BigDecimal)}.
 * Covers full fill, partial fill, overfill rejection, and concurrent fills.
 */
class OrderFillTest {

    private Order newOrder(BigDecimal quantity) {
        return new Order(CurrencyPair.EUR_USD, Side.BUY, new BigDecimal("1.10"), quantity, "user1");
    }

    @Test
    void fullFill_setsStatusToFilled() {
        Order order = newOrder(new BigDecimal("10"));

        order.fill(new BigDecimal("10"));

        assertEquals(BigDecimal.ZERO, order.getRemainingQuantity());
        assertEquals(OrderStatus.FILLED, order.getStatus());
    }

    @Test
    void partialFill_setsStatusToPartiallyFilled() {
        Order order = newOrder(new BigDecimal("10"));

        order.fill(new BigDecimal("3"));

        assertEquals(new BigDecimal("7"), order.getRemainingQuantity());
        assertEquals(OrderStatus.PARTIALLY_FILLED, order.getStatus());
    }

    @Test
    void multipleFills_accumulateCorrectly() {
        Order order = newOrder(new BigDecimal("10"));

        order.fill(new BigDecimal("4"));
        order.fill(new BigDecimal("4"));
        order.fill(new BigDecimal("2"));

        assertEquals(BigDecimal.ZERO, order.getRemainingQuantity());
        assertEquals(OrderStatus.FILLED, order.getStatus());
    }

    @Test
    void overfill_throwsIllegalArgumentException() {
        Order order = newOrder(new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> order.fill(new BigDecimal("10")));
        // remaining quantity must be unchanged
        assertEquals(new BigDecimal("5"), order.getRemainingQuantity());
    }

    @Test
    void nullFill_throwsIllegalArgumentException() {
        Order order = newOrder(new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> order.fill(null));
    }

    @Test
    void zeroFill_throwsIllegalArgumentException() {
        Order order = newOrder(new BigDecimal("5"));

        assertThrows(IllegalArgumentException.class, () -> order.fill(BigDecimal.ZERO));
    }

    @Test
    void concurrentFills_noRaceCondition() throws InterruptedException {
        // 10 threads each fill 1 unit from an order of size 10 — total must reach 0
        int threads = 10;
        Order order = newOrder(new BigDecimal(threads));
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CyclicBarrier barrier = new CyclicBarrier(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                    order.fill(BigDecimal.ONE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        for (Future<?> f : futures) {
            assertDoesNotThrow(() -> f.get());
        }

        assertEquals(BigDecimal.ZERO, order.getRemainingQuantity());
        assertEquals(OrderStatus.FILLED, order.getStatus());
    }
}
