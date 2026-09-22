package com.venturelens.analysis;

import com.venturelens.model.DecisionTier;
import com.venturelens.model.VentureIdea;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom Rule-Based Natural Language Processing & Idea Validation Engine.
 * Built strictly with standard Java SE (java.util.regex, java.util).
 *
 * Pipeline features:
 * 1. Tokenization, punctuation removal, and standard English stop-word filtering.
 * 2. Lookbehind regex negation detection (e.g., "no demand", "lack of competition", "not feasible").
 * 3. Categorized concept dictionaries for Market, Competition/Moat, and Feasibility.
 * 4. Composite weighted decision scoring:
 *    Overall = (Market * 0.30) + (Feasibility * 0.30) + (Competition * 0.20) + (ConceptDepth * 0.20)
 * 5. Decision Tier assignment: STRONG_GO (>=80), GO (65-79), CAUTION (50-64), PIVOT (<50).
 * 6. Dynamic 4-Box SWOT classification and critical failure risk extraction.
 */
public class NLPValidatorEngine {

    // Common English stop-words
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but",
            "by", "can", "cannot", "could", "did", "do", "does", "doing", "down", "during", "each", "few",
            "for", "from", "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself",
            "him", "himself", "his", "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just",
            "me", "more", "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once",
            "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "should",
            "so", "some", "such", "than", "that", "the", "their", "theirs", "them", "themselves", "then",
            "there", "these", "they", "this", "those", "through", "to", "too", "under", "until", "up",
            "very", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why",
            "with", "would", "you", "your", "yours", "yourself", "yourselves"
    ));

    // Regex pattern with lookbehind negation detecting phrases like "no demand", "lack of moat", etc.
    // Matches when preceded by (no|not|never|lack of|hardly|scarcely|without)
    private static final Pattern NEGATION_PATTERN = Pattern.compile(
            "(?i)(?:\\b(?:no|not|never|without|lacks?|lacking)\\s+|\\black\\s+of\\s+)([a-z0-9_-]+)"
    );

    // Concept Dictionaries
    private static final Set<String> MARKET_TERMS = new HashSet<>(Arrays.asList(
            "market", "b2b", "enterprise", "smb", "consumer", "tam", "sam", "som", "demand", "arr", "mrr",
            "retention", "churn", "recurring", "subscription", "expansion", "global", "segment", "volume",
            "cac", "ltv", "pipeline", "revenue", "contracts", "buyers", "growth", "scale", "customers",
            "monetization", "paying", "pricing", "marketplace", "vertical"
    ));

    private static final Set<String> COMPETITION_TERMS = new HashSet<>(Arrays.asList(
            "moat", "proprietary", "patent", "patented", "defensible", "differentiation", "network",
            "switching", "barrier", "first-mover", "monopoly", "ip", "lock-in", "ecosystem", "niche",
            "specialized", "exclusive", "advantage", "unmatched", "unique", "defensibility"
    ));

    private static final Set<String> FEASIBILITY_TERMS = new HashSet<>(Arrays.asList(
            "api", "automated", "cloud", "mvp", "prototype", "architecture", "algorithm", "algorithms",
            "devops", "infrastructure", "latency", "throughput", "integration", "compliance", "security",
            "pipeline", "scalable", "microservices", "database", "sdk", "modular", "engine", "tested",
            "framework", "deploy", "distributed", "serverless", "analytics", "ai", "ml", "telemetry"
    ));

    private static final Set<String> RISK_TERMS = new HashSet<>(Arrays.asList(
            "unproven", "commoditized", "crowded", "regulation", "regulatory", "liability", "expensive",
            "slow", "fragile", "dependency", "churn", "friction", "burn", "unclear", "doubtful", "risky"
    ));

    /**
     * Executes the comprehensive rule-based evaluation pipeline on a VentureIdea.
     */
    public VentureIdea evaluate(VentureIdea idea) {
        String combined = (idea.getStartupName() + " "
                + idea.getTargetCustomer() + " "
                + idea.getProblemStatement() + " "
                + idea.getProposedSolution() + " "
                + idea.getBusinessModel()).toLowerCase();

        // 1. Detect Negated Concept Tokens using Lookbehind Negation Pipeline
        Set<String> negatedConcepts = extractNegations(combined);

        // 2. Tokenize and filter stop-words
        List<String> tokens = tokenize(combined);

        // 3. Count concept hits
        int marketHits = 0;
        int competitionHits = 0;
        int feasibilityHits = 0;
        int riskHits = 0;

        for (String t : tokens) {
            if (MARKET_TERMS.contains(t)) {
                if (negatedConcepts.contains(t)) {
                    riskHits += 2;
                } else {
                    marketHits++;
                }
            }
            if (COMPETITION_TERMS.contains(t)) {
                if (negatedConcepts.contains(t)) {
                    riskHits += 2;
                } else {
                    competitionHits++;
                }
            }
            if (FEASIBILITY_TERMS.contains(t)) {
                if (negatedConcepts.contains(t)) {
                    riskHits += 2;
                } else {
                    feasibilityHits++;
                }
            }
            if (RISK_TERMS.contains(t)) {
                riskHits++;
            }
        }

        // 4. Calculate Sub-Scores (Normalized to 0 - 100)
        double marketScore = calculateSubScore(marketHits, tokens.size(), 3, negatedConcepts.contains("demand"));
        double feasibilityScore = calculateSubScore(feasibilityHits, tokens.size(), 4, negatedConcepts.contains("feasible"));
        double competitionScore = calculateSubScore(competitionHits, tokens.size(), 2, negatedConcepts.contains("moat") || negatedConcepts.contains("competition"));
        double conceptDepth = calculateDepthScore(tokens.size(), idea);

        // Deduct risk penalties
        double riskPenalty = Math.min(30.0, riskHits * 5.0 + negatedConcepts.size() * 6.0);
        marketScore = Math.max(15.0, marketScore - (negatedConcepts.contains("demand") ? 35.0 : 0.0));
        competitionScore = Math.max(15.0, competitionScore - (negatedConcepts.contains("competition") ? 25.0 : 0.0));
        feasibilityScore = Math.max(15.0, feasibilityScore - (negatedConcepts.contains("feasible") ? 30.0 : 0.0));

        // 5. Compute Weighted Composite Score
        // Overall = (Market * 0.30) + (Feasibility * 0.30) + (Competition * 0.20) + (ConceptDepth * 0.20) - Penalty
        double rawComposite = (marketScore * 0.30)
                + (feasibilityScore * 0.30)
                + (competitionScore * 0.20)
                + (conceptDepth * 0.20)
                - (riskPenalty * 0.25);

        double overallScore = Math.round(Math.min(100.0, Math.max(10.0, rawComposite)) * 100.0) / 100.0;
        marketScore = Math.round(marketScore * 100.0) / 100.0;
        feasibilityScore = Math.round(feasibilityScore * 100.0) / 100.0;
        competitionScore = Math.round(competitionScore * 100.0) / 100.0;
        conceptDepth = Math.round(conceptDepth * 100.0) / 100.0;

        idea.setOverallScore(overallScore);
        idea.setMarketScore(marketScore);
        idea.setFeasibilityScore(feasibilityScore);
        idea.setCompetitionScore(competitionScore);
        idea.setDepthScore(conceptDepth);

        // 6. Assign Decision Tier
        DecisionTier tier = DecisionTier.fromScore(overallScore);
        idea.setDecisionTier(tier);

        // 7. Generate 4-Box SWOT and Failure Risks
        populateSwotAndRisks(idea, marketHits, competitionHits, feasibilityHits, negatedConcepts, riskHits);

        return idea;
    }

    private Set<String> extractNegations(String text) {
        Set<String> negations = new HashSet<>();
        Matcher matcher = NEGATION_PATTERN.matcher(text);
        while (matcher.find()) {
            String negatedTerm = matcher.group(1).toLowerCase();
            negations.add(negatedTerm);
        }
        return negations;
    }

    private List<String> tokenize(String text) {
        List<String> list = new ArrayList<>();
        String[] rawTokens = text.split("[^a-zA-Z0-9_-]+");
        for (String rt : rawTokens) {
            String token = rt.trim().toLowerCase();
            if (token.length() > 1 && !STOP_WORDS.contains(token)) {
                list.add(token);
            }
        }
        return list;
    }

    private double calculateSubScore(int hits, int totalTokens, int benchmarkHits, boolean severeNegation) {
        if (severeNegation) {
            return 25.0;
        }
        double ratio = (double) hits / Math.max(1, benchmarkHits);
        double score = 50.0 + Math.min(45.0, ratio * 35.0);
        return Math.min(95.0, Math.max(20.0, score));
    }

    private double calculateDepthScore(int tokenCount, VentureIdea idea) {
        int length = idea.getProblemStatement().length() + idea.getProposedSolution().length();
        if (length < 40) return 30.0;
        if (length < 120) return 55.0;
        if (length < 250) return 75.0;
        return 92.0;
    }

    private void populateSwotAndRisks(VentureIdea idea, int marketHits, int competitionHits,
                                     int feasibilityHits, Set<String> negations, int riskHits) {
        List<String> s = new ArrayList<>();
        List<String> w = new ArrayList<>();
        List<String> o = new ArrayList<>();
        List<String> t = new ArrayList<>();
        List<String> r = new ArrayList<>();

        // Strengths
        if (feasibilityHits >= 2) {
            s.add("Engineered technical architecture with concrete feasibility markers.");
        }
        if (competitionHits >= 1) {
            s.add("Articulated competitive moat and proprietary differentiation mechanisms.");
        }
        if (idea.getBusinessModel() != null && idea.getBusinessModel().toLowerCase().contains("subscription")) {
            s.add("Predictable recurring revenue model with compounding SaaS dynamics.");
        }
        if (s.isEmpty()) {
            s.add("Clear core problem articulation addressing identified customer friction.");
        }

        // Weaknesses
        if (competitionHits == 0 || negations.contains("moat")) {
            w.add("Limited structural defensibility; vulnerable to rapid incumbent copying.");
        }
        if (feasibilityHits < 2) {
            w.add("Technical execution details require specification and validation.");
        }
        if (idea.getTargetCustomer() != null && idea.getTargetCustomer().length() < 15) {
            w.add("Target customer segment lacks granular ICP definition.");
        }
        if (w.isEmpty()) {
            w.add("Early-stage brand presence requiring accelerated enterprise proof points.");
        }

        // Opportunities
        if (marketHits >= 2) {
            o.add("Expanding addressable market demand across specified customer segments.");
        }
        o.add("Favorable tailwinds for modern cloud-native automated tooling.");
        o.add("Potential for land-and-expand monetization via enterprise tiered rollouts.");

        // Threats
        if (riskHits > 0 || negations.contains("demand")) {
            t.add("Negative market sentiment flags detected regarding market demand or feasibility.");
        }
        t.add("Aggressive feature expansion by established market incumbents.");
        t.add("Potential enterprise sales cycle friction and procurement latency.");

        // Critical Failure Risks
        if (negations.contains("demand")) {
            r.add("CRITICAL: Negative demand indicator detected ('no demand' / 'lacks demand'). Severe market validation deficit.");
        }
        if (negations.contains("competition") || negations.contains("moat")) {
            r.add("HIGH: Lack of defensible moat exposes margins to price erosion.");
        }
        if (negations.contains("feasible")) {
            r.add("HIGH: Technical feasibility flagged as questionable or constrained.");
        }
        if (r.isEmpty()) {
            r.add("Execution timeline risk: Slower than expected enterprise contract conversion.");
            r.add("Go-to-market risk: High outbound sales customer acquisition cost (CAC).");
        }

        idea.setStrengths(s);
        idea.setWeaknesses(w);
        idea.setOpportunities(o);
        idea.setThreats(t);
        idea.setRisks(r);
    }
}
