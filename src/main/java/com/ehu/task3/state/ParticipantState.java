package com.ehu.task3.state;

import com.ehu.task3.entity.Currency;
import com.ehu.task3.entity.Participant;

public interface ParticipantState {
    boolean adjustBalance(Participant participant, Currency currency, double amount);
}

