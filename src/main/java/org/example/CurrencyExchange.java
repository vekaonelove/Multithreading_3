package org.example;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CurrencyExchange {
    public static void main(String[] args) {
        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant("TraderA", 10000, 10, 5));
        participants.add(new Participant("TraderB", 20000, 2, 10));
        participants.add(new Participant("TraderC", 5000, 8, 20));

        Exchange exchange = Exchange.getInstance(participants);

        Thread matcher = new Thread(exchange::matchOrders, "Matcher");
        matcher.start();

        AtomicBoolean running = new AtomicBoolean(true);
        Thread balancesPrinter = new Thread(() -> {
            while (running.get()) {
                try {
                    exchange.printBalances();
                    TimeUnit.MILLISECONDS.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "BalancePrinter");
        balancesPrinter.start();

        Thread orderPlacer = new Thread(() -> {
            Random rnd = new Random();
            Currency[] cur = Currency.values();
            while (running.get()) {
                // Pick a random participant and random trade
                Participant p = participants.get(rnd.nextInt(participants.size()));
                Currency from = cur[rnd.nextInt(cur.length)];
                Currency to = cur[rnd.nextInt(cur.length)];
                if (from == to) continue;
                double amount = 1 + rnd.nextInt(10);
                Order o = new Order(p, from, to, amount, 0.0);
                exchange.placeOrder(o);
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "OrderPlacer");
        orderPlacer.start();

        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        running.set(false);
        matcher.interrupt();
        balancesPrinter.interrupt();
        orderPlacer.interrupt();
        System.out.println("Exchange simulation finished.");
    }
}
