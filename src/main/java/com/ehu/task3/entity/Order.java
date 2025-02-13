package com.ehu.task3.entity;

public record Order(Participant participant, Currency from, Currency to, double amount) {

    @Override
    public String toString() {
        return String.format("Order[Trader: %s, %s → %s, Amount: %.2f]", participant.getName(), from, to, amount);
    }
}
