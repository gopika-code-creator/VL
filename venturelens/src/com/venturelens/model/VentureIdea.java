package com.venturelens.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a venture evaluation including user inputs, NLP-derived scores,
 * decision tier, 4-box SWOT lists, and critical failure risks.
 */
public class VentureIdea {
    private int id;
    private int userId;
    private String startupName;
    private String targetCustomer;
    private String problemStatement;
    private String proposedSolution;
    private String businessModel;

    // Component and overall scores (0.0 to 100.0)
    private double overallScore;
    private double marketScore;
    private double feasibilityScore;
    private double competitionScore;
    private double depthScore;

    private DecisionTier decisionTier;

    // 4-Box SWOT lists
    private List<String> strengths = new ArrayList<>();
    private List<String> weaknesses = new ArrayList<>();
    private List<String> opportunities = new ArrayList<>();
    private List<String> threats = new ArrayList<>();

    // Critical failure risks
    private List<String> risks = new ArrayList<>();

    private Timestamp createdAt;

    public VentureIdea() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStartupName() {
        return startupName;
    }

    public void setStartupName(String startupName) {
        this.startupName = startupName;
    }

    public String getTargetCustomer() {
        return targetCustomer;
    }

    public void setTargetCustomer(String targetCustomer) {
        this.targetCustomer = targetCustomer;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getProposedSolution() {
        return proposedSolution;
    }

    public void setProposedSolution(String proposedSolution) {
        this.proposedSolution = proposedSolution;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getMarketScore() {
        return marketScore;
    }

    public void setMarketScore(double marketScore) {
        this.marketScore = marketScore;
    }

    public double getFeasibilityScore() {
        return feasibilityScore;
    }

    public void setFeasibilityScore(double feasibilityScore) {
        this.feasibilityScore = feasibilityScore;
    }

    public double getCompetitionScore() {
        return competitionScore;
    }

    public void setCompetitionScore(double competitionScore) {
        this.competitionScore = competitionScore;
    }

    public double getDepthScore() {
        return depthScore;
    }

    public void setDepthScore(double depthScore) {
        this.depthScore = depthScore;
    }

    public DecisionTier getDecisionTier() {
        return decisionTier;
    }

    public void setDecisionTier(DecisionTier decisionTier) {
        this.decisionTier = decisionTier;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<String> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<String> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(List<String> opportunities) {
        this.opportunities = opportunities;
    }

    public List<String> getThreats() {
        return threats;
    }

    public void setThreats(List<String> threats) {
        this.threats = threats;
    }

    public List<String> getRisks() {
        return risks;
    }

    public void setRisks(List<String> risks) {
        this.risks = risks;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
