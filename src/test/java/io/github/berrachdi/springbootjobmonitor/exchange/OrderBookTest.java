package io.github.berrachdi.springbootjobmonitor.exchange;

import io.github.berrachdi.springbootjobmonitor.exchange.domain.*;
import io.github.berrachdi.springbootjobmonitor.exchange.engine.OrderBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OrderBook}.
 * Verifies matching logic, partial fills, price priority, and thread-safety.
 */
class OrderBookTest {

    private OrderBook book;

    @BeforeEach
    void setUp() {
        book = new OrderBook(CurrencyPair.EUR_USD);
    }

    private Order buyOrder(String price, String qty) {
        return new Order(CurrencyPair.EUR_USD, Side.BUY, new BigDecimal(price), new BigDecimal(qty), "buyer");
    }

    private Order sellOrder(String price, String qty) {
        return new Order(CurrencyPair.EUR_USD, Side.SELL, new BigDecimal(price), new BigDecimal(qty), "seller");
    }

    @Test
    void noMatch_whenPricesDoNotCross() {
        book.submitOrder(buyOrder("1.09", "5"));
        book.submitOrder(sellOrder("1.11", "5"));

        assertEquals(1, book.getBuyOrderCount());
        assertEquals(1, book.getSellOrderCount());
    }

    @Test
    void fullMatch_producesOneTrade() {
        book.submitOrder(sellOrder("1.10", "5"));
        List<Trade> trades = book.submitOrder(buyOrder("1.10", "5"));

        assertEquals(1, trades.size());
        assertEquals(new BigDecimal("5"), trades.get(0).getQuantity());
        assertEquals(0, book.getBuyOrderCount());
        assertEquals(0, book.getSellOrderCount());
    }

    @Test
    void partialMatch_leavesRemainderOnBook() {
        book.submitOrder(sellOrder("1.10", "3"));
        List<Trade> trades = book.submitOrder(buyOrder("1.10", "5"));

        assertEquals(1, trades.size());
        assertEquals(new BigDecimal("3"), trades.get(0).getQuantity());
        assertEquals(1, book.getBuyOrderCount());   // 2 units remain on buy side
        assertEquals(0, book.getSellOrderCount());
    }

    @Test
    void buyAtHigherPrice_matchesAtAskPrice() {
        book.submitOrder(sellOrder("1.10", "5"));
        List<Trade> trades = book.submitOrder(buyOrder("1.12", "5"));

        assertEquals(1, trades.size());
        // trade price must be the resting order's price
        assertEquals(new BigDecimal("1.10"), trades.get(0).getPrice());
    }

    @Test
    void multipleRestingSellOrders_matchedInPriceOrder() {
        book.submitOrder(sellOrder("1.12", "2"));
        book.submitOrder(sellOrder("1.10", "2"));   // cheaper — matched first
        book.submitOrder(sellOrder("1.11", "2"));

        List<Trade> trades = book.submitOrder(buyOrder("1.15", "4"));

        assertEquals(2, trades.size());
        assertEquals(new BigDecimal("1.10"), trades.get(0).getPrice());
        assertEquals(new BigDecimal("1.11"), trades.get(1).getPrice());
    }

    @Test
    void concurrentSubmissions_noDeadlockOrDataCorruption() throws InterruptedException {
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CyclicBarrier barrier = new CyclicBarrier(threads);
        List<Future<List<Trade>>> futures = new ArrayList<>();

        // 10 buyers and 10 sellers all at the same price — every order should match
        for (int i = 0; i < threads / 2; i++) {
            futures.add(executor.submit(() -> {
                barrier.await(5, TimeUnit.SECONDS);
                return book.submitOrder(buyOrder("1.10", "1"));
            }));
            futures.add(executor.submit(() -> {
                barrier.await(5, TimeUnit.SECONDS);
                return book.submitOrder(sellOrder("1.10", "1"));
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        long totalTrades = futures.stream()
                .mapToLong(f -> {
                    try { return f.get().size(); } catch (Exception e) { return 0; }
                })
                .sum();

        // All 10 pairs should have matched — each match produces 1 trade counted twice
        // (once in buy future, once in sell future), so total = 10
        assertTrue(totalTrades >= 10,
                "Expected at least 10 trades but got " + totalTrades);
        assertEquals(0, book.getBuyOrderCount() + book.getSellOrderCount(),
                "No orders should remain on the book after full matching");
    }
}
