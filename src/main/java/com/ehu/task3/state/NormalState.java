package com.ehu.task3.state;

import com.ehu.task3.entity.Currency;
import com.ehu.task3.entity.Participant;

public class NormalState implements ParticipantState {
    @Override
    public boolean adjustBalance(Participant participant, Currency currency, double amount) {
        switch (currency) {
            case TUG:
                if (participant.getTugBalance() + amount < 0) return false;
                participant.setTugBalance(participant.getTugBalance() + amount);
                break;
            case USD:
                if (participant.getUsdBalance() + amount < 0) return false;
                participant.setUsdBalance(participant.getUsdBalance() + amount);  // Use setter here
                break;
            case EUR:
                if (participant.getEurBalance() + amount < 0) return false;
                participant.setEurBalance(participant.getEurBalance() + amount);  // Use setter here
                break;
        }
        return true;
    }
}
