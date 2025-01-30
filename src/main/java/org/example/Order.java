package org.example;

public class Order {
    final Participant participant;
    final Currency from;
    final Currency to;
    final double amount;
    double price;

    public Order(Participant participant, Currency from, Currency to, double amount, double price) {
        this.participant = participant;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.price = price;
    }
}
