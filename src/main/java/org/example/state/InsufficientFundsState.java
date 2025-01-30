package org.example.state;

import org.example.Currency;
import org.example.Participant;

public class InsufficientFundsState implements ParticipantState {
    @Override
    public boolean adjustBalance(Participant participant, Currency currency, double amount) {
        System.out.println(participant.getName() + " -> Insufficient funds for " + currency);
        return false;
    }
}

