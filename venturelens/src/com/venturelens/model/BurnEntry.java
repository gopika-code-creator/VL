package com.venturelens.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a single ledger entry for BurnWatch cash flow tracking.
 */
public class BurnEntry {
    public enum EntryType {
        EXPENSE,
        REVENUE
    }

    private int id;
    private int ventureId;
    private String monthLabel;
    private String category;
    private EntryType entryType;
    private BigDecimal amount;
    private String notes;
    private Timestamp createdAt;

    public BurnEntry() {
    }

    public BurnEntry(int id, int ventureId, String monthLabel, String category, EntryType entryType, BigDecimal amount, String notes) {
        this.id = id;
        this.ventureId = ventureId;
        this.monthLabel = monthLabel;
        this.category = category;
        this.entryType = entryType;
        this.amount = amount;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVentureId() {
        return ventureId;
    }

    public void setVentureId(int ventureId) {
        this.ventureId = ventureId;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public void setMonthLabel(String monthLabel) {
        this.monthLabel = monthLabel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
