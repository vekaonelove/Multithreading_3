package org.example.state;

import org.example.Currency;
import org.example.Participant;

public class NormalState implements ParticipantState {
    @Override
    public boolean adjustBalance(Participant participant, Currency currency, double amount) {
        switch (currency) {
            case TUG:
                if (participant.getTugBalance() + amount < 0) return false;
                participant.setTugBalance(participant.getTugBalance() + amount);  // Fix assignment here
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
