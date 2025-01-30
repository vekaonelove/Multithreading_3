package org.example.state;

import org.example.Currency;
import org.example.Participant;

public interface ParticipantState {
    boolean adjustBalance(Participant participant, Currency currency, double amount);
}

