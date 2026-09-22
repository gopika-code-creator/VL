package com.venturelens.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages financial cash balances, monthly ledger calculations, Net Burn,
 * Survival Runway in months, and projected Zero-Cash Date.
 */
public class FinancialLedger {
    private BigDecimal startingCashBalance;
    private final List<BurnEntry> entries = new ArrayList<>();

    public FinancialLedger() {
        this.startingCashBalance = new BigDecimal("250000.00");
    }

    public FinancialLedger(BigDecimal startingCashBalance) {
        this.startingCashBalance = startingCashBalance;
    }

    public BigDecimal getStartingCashBalance() {
        return startingCashBalance;
    }

    public void setStartingCashBalance(BigDecimal startingCashBalance) {
        this.startingCashBalance = startingCashBalance;
    }

    public List<BurnEntry> getEntries() {
        return entries;
    }

    public void addEntry(BurnEntry entry) {
        entries.add(entry);
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public BigDecimal getTotalMonthlyExpenses() {
        BigDecimal total = BigDecimal.ZERO;
        for (BurnEntry e : entries) {
            if (e.getEntryType() == BurnEntry.EntryType.EXPENSE) {
                total = total.add(e.getAmount());
            }
        }
        return total;
    }

    public BigDecimal getTotalMonthlyRevenue() {
        BigDecimal total = BigDecimal.ZERO;
        for (BurnEntry e : entries) {
            if (e.getEntryType() == BurnEntry.EntryType.REVENUE) {
                total = total.add(e.getAmount());
            }
        }
        return total;
    }

    public BigDecimal getNetMonthlyBurn() {
        return getTotalMonthlyExpenses().subtract(getTotalMonthlyRevenue());
    }

    /**
     * Calculates survival runway in months = Cash Balance / Net Monthly Burn.
     * Returns Double.POSITIVE_INFINITY if net burn is <= 0 (default profitable/cashflow-positive).
     */
    public double getRunwayMonths() {
        BigDecimal netBurn = getNetMonthlyBurn();
        if (netBurn.compareTo(BigDecimal.ZERO) <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (startingCashBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        return startingCashBalance.divide(netBurn, 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Estimates Zero-Cash Date by adding runway months to current date.
     */
    public String getZeroCashDateEstimate() {
        double runway = getRunwayMonths();
        if (Double.isInfinite(runway)) {
            return "Cashflow Positive (Indefinite)";
        }
        if (runway <= 0.0) {
            return "Immediate Deficit";
        }
        long days = (long) (runway * 30.4375);
        LocalDate zeroDate = LocalDate.now().plusDays(days);
        return zeroDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}
