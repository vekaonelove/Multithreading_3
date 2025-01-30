package org.example;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Exchange {
    private static Exchange instance;
    private static final Lock lock = new ReentrantLock();

    private final List<Participant> participants;
    private final BlockingQueue<Order> ordersQueue = new LinkedBlockingQueue<>();

    private double tugToUsd = 3500.0;
    private double tugToEur = 3800.0;

    private Exchange(List<Participant> participants) {
        this.participants = participants;
    }

    public static Exchange getInstance(List<Participant> participants) {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new Exchange(participants);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public void placeOrder(Order order) {
        ordersQueue.offer(order);  // Place order in the queue
    }

    public void matchOrders() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Order order = ordersQueue.take();
                processOrder(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processOrder(Order order) {
        if (!order.participant.adjustBalance(order.from, -order.amount)) {
            System.out.println(order.participant.name + " => Order Rejected (not enough " + order.from + ")");
            return;
        }

        double received = 0;
        if (order.from == Currency.TUG) {
            received = order.to == Currency.USD
                    ? (order.amount / tugToUsd)
                    : (order.amount / tugToEur);
        } else if (order.to == Currency.TUG) {
            received = order.from == Currency.USD
                    ? (order.amount * tugToUsd)
                    : (order.amount * tugToEur);
        } else {
            if (order.from == Currency.USD && order.to == Currency.EUR) {
                double inTug = order.amount * tugToUsd;
                received = inTug / tugToEur;
            } else {
                double inTug = order.amount * tugToEur;
                received = inTug / tugToUsd;
            }
        }

        boolean success = order.participant.adjustBalance(order.to, received);
        if (!success) {
            order.participant.adjustBalance(order.from, order.amount);
            System.out.println(order.participant.name + " => Order Failed (unexpected error)");
            return;
        }

        System.out.println(order.participant.name + " => Traded " + order.amount + " " + order.from
                + " for " + String.format("%.4f", received) + " " + order.to);

        if (order.to == Currency.USD || order.from == Currency.USD) {
            tugToUsd += (Math.random() - 0.5) * 10;
            if (tugToUsd < 1) tugToUsd = 1;
        }
        if (order.to == Currency.EUR || order.from == Currency.EUR) {
            tugToEur += (Math.random() - 0.5) * 10;
            if (tugToEur < 1) tugToEur = 1;
        }
    }

    public void printBalances() {
        System.out.println("\nCurrent Balances (Rate: 1 USD=" + (int)tugToUsd + " TUG, 1 EUR=" + (int)tugToEur + " TUG):");
        for (Participant p : participants) {
            System.out.printf("%s -> TUG: %.2f, USD: %.2f, EUR: %.2f%n",
                    p.name, p.tugBalance, p.usdBalance, p.eurBalance);
        }
        System.out.println();
    }
}
