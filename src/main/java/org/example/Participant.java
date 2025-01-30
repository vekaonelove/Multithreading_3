package org.example;

import org.example.state.NormalState;
import org.example.state.ParticipantState;

public class Participant {
    final String name;
    double tugBalance;
    double usdBalance;
    double eurBalance;
    private ParticipantState state;

    public Participant(String name, double tug, double usd, double eur) {
        this.name = name;
        this.tugBalance = tug;
        this.usdBalance = usd;
        this.eurBalance = eur;
        this.state = new NormalState();
    }

    public String getName() {
        return name;
    }

    public double getTugBalance() {
        return tugBalance;
    }

    public void setTugBalance(double tugBalance) {
        this.tugBalance = tugBalance;
    }

    public double getUsdBalance() {
        return usdBalance;
    }

    public void setUsdBalance(double usdBalance) {
        this.usdBalance = usdBalance;
    }

    public double getEurBalance() {
        return eurBalance;
    }

    public void setEurBalance(double eurBalance) {
        this.eurBalance = eurBalance;
    }

    public ParticipantState getState() {
        return state;
    }

    public void setState(ParticipantState state) {
        this.state = state;
    }

    public boolean adjustBalance(Currency currency, double amount) {
        return state.adjustBalance(this, currency, amount);
    }
}
