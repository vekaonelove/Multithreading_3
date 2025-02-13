package com.ehu.task3.state;

import com.ehu.task3.entity.Currency;
import com.ehu.task3.entity.Participant;

public class InsufficientFundsState implements ParticipantState {
    @Override
    public boolean adjustBalance(Participant participant, Currency currency, double amount) {
        System.out.println(participant.getName() + " -> Insufficient funds for " + currency);
        return false;
    }
}

