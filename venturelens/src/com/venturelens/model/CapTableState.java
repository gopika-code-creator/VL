package com.venturelens.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Handles CapTable equity splits and investment dilution simulations with high precision
 * using java.math.BigDecimal to prevent floating-point rounding errors.
 */
public class CapTableState {
    private BigDecimal founder1InitialPct; // e.g. 50.00
    private BigDecimal founder2InitialPct; // e.g. 40.00
    private BigDecimal esopInitialPct;     // e.g. 10.00

    private BigDecimal preMoneyValuation;   // e.g. 6,000,000.00
    private BigDecimal investmentAmount;    // e.g. 1,500,000.00

    public CapTableState() {
        this.founder1InitialPct = new BigDecimal("50.00");
        this.founder2InitialPct = new BigDecimal("40.00");
        this.esopInitialPct = new BigDecimal("10.00");
        this.preMoneyValuation = new BigDecimal("5000000.00");
        this.investmentAmount = new BigDecimal("1000000.00");
    }

    public CapTableState(BigDecimal f1, BigDecimal f2, BigDecimal esop, BigDecimal preMoney, BigDecimal investment) {
        this.founder1InitialPct = f1;
        this.founder2InitialPct = f2;
        this.esopInitialPct = esop;
        this.preMoneyValuation = preMoney;
        this.investmentAmount = investment;
    }

    public BigDecimal getPostMoneyValuation() {
        return preMoneyValuation.add(investmentAmount);
    }

    public BigDecimal getInvestorOwnershipPct() {
        BigDecimal post = getPostMoneyValuation();
        if (post.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return investmentAmount.multiply(new BigDecimal("100"))
                .divide(post, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal getRetentionFactor() {
        BigDecimal post = getPostMoneyValuation();
        if (post.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE;
        }
        return preMoneyValuation.divide(post, 6, RoundingMode.HALF_UP);
    }

    public BigDecimal getFounder1DilutedPct() {
        return founder1InitialPct.multiply(getRetentionFactor())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getFounder2DilutedPct() {
        return founder2InitialPct.multiply(getRetentionFactor())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getEsopDilutedPct() {
        return esopInitialPct.multiply(getRetentionFactor())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getFounder1InitialPct() {
        return founder1InitialPct;
    }

    public void setFounder1InitialPct(BigDecimal founder1InitialPct) {
        this.founder1InitialPct = founder1InitialPct;
    }

    public BigDecimal getFounder2InitialPct() {
        return founder2InitialPct;
    }

    public void setFounder2InitialPct(BigDecimal founder2InitialPct) {
        this.founder2InitialPct = founder2InitialPct;
    }

    public BigDecimal getEsopInitialPct() {
        return esopInitialPct;
    }

    public void setEsopInitialPct(BigDecimal esopInitialPct) {
        this.esopInitialPct = esopInitialPct;
    }

    public BigDecimal getPreMoneyValuation() {
        return preMoneyValuation;
    }

    public void setPreMoneyValuation(BigDecimal preMoneyValuation) {
        this.preMoneyValuation = preMoneyValuation;
    }

    public BigDecimal getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(BigDecimal investmentAmount) {
        this.investmentAmount = investmentAmount;
    }
}
