package com.ehu.task3.entity;

import com.ehu.task3.state.NormalState;
import com.ehu.task3.state.ParticipantState;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Participant {
    private String name;
    private double tugBalance;
    private double usdBalance;
    private double eurBalance;
    private ParticipantState state;
    private Lock balanceLock = new ReentrantLock();

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

    public double getUsdBalance() {
        return usdBalance;
    }

    public double getEurBalance() {
        return eurBalance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTugBalance(double tugBalance) {
        this.tugBalance = tugBalance;
    }

    public void setUsdBalance(double usdBalance) {
        this.usdBalance = usdBalance;
    }

    public void setEurBalance(double eurBalance) {
        this.eurBalance = eurBalance;
    }

    public void setState(ParticipantState state) {
        this.state = state;
    }

    public void setBalanceLock(Lock balanceLock) {
        this.balanceLock = balanceLock;
    }

    public boolean adjustBalance(Currency currency, double amount) {
        balanceLock.lock();
        try {
            return state.adjustBalance(this, currency, amount);
        } finally {
            balanceLock.unlock();
        }
    }
}
