package com.ehu.task3;

import com.ehu.task3.entity.Currency;
import com.ehu.task3.entity.Order;
import com.ehu.task3.entity.Participant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CurrencyExchange {
    private static final Logger logger = LogManager.getLogger(CurrencyExchange.class);

    public static void main(String[] args) {
        List<Participant> participants = loadParticipantsFromFile();
        if (participants.isEmpty()) {
            logger.error("No participants loaded. Exiting program.");
            return;
        }

        Exchange exchange = Exchange.getInstance(participants);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(exchange);

        executor.submit(() -> {
            try {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                Currency[] currencies = Currency.values();

                while (!Thread.currentThread().isInterrupted()) {
                    Participant participant = participants.get(rnd.nextInt(participants.size()));
                    Currency from = currencies[rnd.nextInt(currencies.length)];
                    Currency to = currencies[rnd.nextInt(currencies.length)];

                    if (from != to) {
                        double amount = 1 + rnd.nextInt(10);
                        Order order = new Order(participant, from, to, amount);
                        exchange.placeOrder(order);
                        logger.info("Placed order: {}", order);
                    }
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
            } catch (InterruptedException e) {
                logger.warn("Order placement thread interrupted.", e);
                Thread.currentThread().interrupt();
            }
            return null;
        });

        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                exchange.printBalances();
                logger.info("Balances printed.");
                TimeUnit.MILLISECONDS.sleep(3000);
            }
            return null;
        });

        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            logger.warn("Main thread interrupted.", e);
            Thread.currentThread().interrupt();
        }
        executor.shutdownNow();
        logger.info("Exchange simulation finished.");
    }

    private static List<Participant> loadParticipantsFromFile() {
        List<Participant> participants = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(CurrencyExchange.class.getClassLoader().getResourceAsStream("participants.txt"))))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                participants.add(new Participant(data[0], Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])));
            }
        } catch (IOException e) {
            logger.error("Error loading participants from file.", e);
        }
        return participants;
    }

}
