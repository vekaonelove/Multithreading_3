package com.ehu.task3;

import com.ehu.task3.entity.Currency;
import com.ehu.task3.entity.Order;
import com.ehu.task3.entity.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Exchange implements Callable<Void> {
    private static final Logger logger = LogManager.getLogger(Exchange.class);
    private static Exchange instance;
    private static final Lock instanceLock = new ReentrantLock();

    private final List<Participant> participants;
    private final Queue<Order> orderQueue = new ConcurrentLinkedQueue<>();
    private final Lock queueLock = new ReentrantLock();

    private double tugToUsd = 3500.0;
    private double tugToEur = 3800.0;

    private Exchange(List<Participant> participants) {
        this.participants = participants;
    }

    public static Exchange getInstance(List<Participant> participants) {
        instanceLock.lock();
        try {
            if (instance == null) {
                instance = new Exchange(participants);
            }
        } finally {
            instanceLock.unlock();
        }
        return instance;
    }

    public void placeOrder(Order order) {
        queueLock.lock();
        try {
            orderQueue.offer(order);
            logger.info("{} placed order: {} {} → {}", order.participant().getName(), order.amount(), order.from(), order.to());
        } finally {
            queueLock.unlock();
        }
    }

    private void processOrder(Order order) {
        Participant participant = order.participant();
        if (!participant.adjustBalance(order.from(), -order.amount())) {
            logger.warn("{} Order Rejected: Not enough {}", participant.getName(), order.from());
            return;
        }

        double received = calculateExchange(order);
        if (!participant.adjustBalance(order.to(), received)) {
            participant.adjustBalance(order.from(), order.amount());
            logger.error("{} Order Failed: Adjustment error", participant.getName());
            return;
        }

        logger.info("{} Traded {} {} for {:.4f} {}", participant.getName(), order.amount(), order.from(), received, order.to());
        updateRates(order);
    }

    private double calculateExchange(Order order) {
        if (order.from() == Currency.TUG) {
            return order.to() == Currency.USD ? order.amount() / tugToUsd : order.amount() / tugToEur;
        } else if (order.to() == Currency.TUG) {
            return order.from() == Currency.USD ? order.amount() * tugToUsd : order.amount() * tugToEur;
        } else {
            double inTug = order.from() == Currency.USD ? order.amount() * tugToUsd : order.amount() * tugToEur;
            return order.to() == Currency.USD ? inTug / tugToUsd : inTug / tugToEur;
        }
    }

    private void updateRates(Order order) {
        if (order.to() == Currency.USD || order.from() == Currency.USD) {
            tugToUsd = Math.max(1, tugToUsd + (Math.random() - 0.5) * 10);
        }
        if (order.to() == Currency.EUR || order.from() == Currency.EUR) {
            tugToEur = Math.max(1, tugToEur + (Math.random() - 0.5) * 10);
        }
    }

    public void printBalances() {
        logger.info("Exchange Rates: 1 USD = {} TUG, 1 EUR = {} TUG", (int) tugToUsd, (int) tugToEur);
        for (Participant p : participants) {
            logger.info("{} Balance → TUG: {:.2f}, USD: {:.2f}, EUR: {:.2f}", p.getName(), p.getTugBalance(), p.getUsdBalance(), p.getEurBalance());
        }
    }

    @Override
    public Void call() {
        while (!Thread.currentThread().isInterrupted()) {
            queueLock.lock();
            try {
                if (!orderQueue.isEmpty()) {
                    processOrder(orderQueue.poll());
                }
            } finally {
                queueLock.unlock();
            }
        }
        return null;
    }
}
