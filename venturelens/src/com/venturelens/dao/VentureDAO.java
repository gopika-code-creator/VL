package com.venturelens.dao;

import com.venturelens.model.DecisionTier;
import com.venturelens.model.VentureIdea;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Data Access Object for Venture records.
 * Uses official JDBC (java.sql.*) with try-with-resources.
 */
public class VentureDAO {

    // Thread-safe in-memory cache for demo/offline resilience
    private static final List<VentureIdea> IN_MEMORY_VENTURES = new CopyOnWriteArrayList<>();

    static {
        // Seed default initial venture
        VentureIdea seed = new VentureIdea();
        seed.setId(1);
        seed.setUserId(1);
        seed.setStartupName("DataPulse AI");
        seed.setTargetCustomer("B2B SaaS engineering leaders and CTOs");
        seed.setProblemStatement("Engineering organizations struggle with silent data pipeline breakages causing massive enterprise revenue loss and customer churn.");
        seed.setProposedSolution("An automated, cloud-native real-time telemetry agent with proprietary anomaly detection algorithms and zero-config SDK integrations.");
        seed.setBusinessModel("B2B Enterprise SaaS Subscription");
        seed.setOverallScore(84.50);
        seed.setMarketScore(88.00);
        seed.setFeasibilityScore(85.00);
        seed.setCompetitionScore(78.00);
        seed.setDepthScore(85.00);
        seed.setDecisionTier(DecisionTier.STRONG_GO);
        seed.setStrengths(Arrays.asList("Proprietary anomaly detection algorithms", "Automated cloud-native telemetry agent", "High switching costs"));
        seed.setWeaknesses(Arrays.asList("Early-stage brand presence", "Requires enterprise security compliance certifications"));
        seed.setOpportunities(Arrays.asList("Expanding enterprise telemetry TAM", "High willingness to pay among mid-market CTOs"));
        seed.setThreats(Arrays.asList("Incumbent legacy APM tool feature additions", "Enterprise procurement cycle latency"));
        seed.setRisks(Arrays.asList("Enterprise compliance timeline friction", "High outbound sales customer acquisition cost"));
        seed.setCreatedAt(new Timestamp(System.currentTimeMillis() - 86400000L * 2));
        IN_MEMORY_VENTURES.add(seed);
    }

    public int saveVenture(VentureIdea idea) throws SQLException {
        String sql = "INSERT INTO ventures (" +
                "user_id, startup_name, target_customer, problem_statement, proposed_solution, " +
                "business_model, overall_score, market_score, feasibility_score, competition_score, " +
                "depth_score, decision_tier, swot_strengths, swot_weaknesses, swot_opportunities, " +
                "swot_threats, risks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idea.getUserId());
            stmt.setString(2, idea.getStartupName());
            stmt.setString(3, idea.getTargetCustomer());
            stmt.setString(4, idea.getProblemStatement());
            stmt.setString(5, idea.getProposedSolution());
            stmt.setString(6, idea.getBusinessModel());
            stmt.setDouble(7, idea.getOverallScore());
            stmt.setDouble(8, idea.getMarketScore());
            stmt.setDouble(9, idea.getFeasibilityScore());
            stmt.setDouble(10, idea.getCompetitionScore());
            stmt.setDouble(11, idea.getDepthScore());
            stmt.setString(12, idea.getDecisionTier().name());
            stmt.setString(13, String.join(";;", idea.getStrengths()));
            stmt.setString(14, String.join(";;", idea.getWeaknesses()));
            stmt.setString(15, String.join(";;", idea.getOpportunities()));
            stmt.setString(16, String.join(";;", idea.getThreats()));
            stmt.setString(17, String.join(";;", idea.getRisks()));

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    idea.setId(id);
                    idea.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    IN_MEMORY_VENTURES.add(idea);
                    return id;
                }
            }
        } catch (SQLException e) {
            // Fallback for offline desktop demo
            int newId = IN_MEMORY_VENTURES.size() + 1;
            idea.setId(newId);
            idea.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            IN_MEMORY_VENTURES.add(idea);
            return newId;
        }

        throw new SQLException("Could not retrieve generated key for venture");
    }

    public List<VentureIdea> getVenturesByUser(int userId) throws SQLException {
        List<VentureIdea> list = new ArrayList<>();
        String sql = "SELECT * FROM ventures WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            // Return from in-memory fallback
            List<VentureIdea> userVentures = new ArrayList<>();
            for (VentureIdea v : IN_MEMORY_VENTURES) {
                if (v.getUserId() == userId) {
                    userVentures.add(v);
                }
            }
            return userVentures;
        }

        return list;
    }

    public boolean deleteVenture(int ventureId) throws SQLException {
        String sql = "DELETE FROM ventures WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ventureId);
            int rows = stmt.executeUpdate();
            IN_MEMORY_VENTURES.removeIf(v -> v.getId() == ventureId);
            return rows > 0;
        } catch (SQLException e) {
            return IN_MEMORY_VENTURES.removeIf(v -> v.getId() == ventureId);
        }
    }

    private VentureIdea mapRow(ResultSet rs) throws SQLException {
        VentureIdea idea = new VentureIdea();
        idea.setId(rs.getInt("id"));
        idea.setUserId(rs.getInt("user_id"));
        idea.setStartupName(rs.getString("startup_name"));
        idea.setTargetCustomer(rs.getString("target_customer"));
        idea.setProblemStatement(rs.getString("problem_statement"));
        idea.setProposedSolution(rs.getString("proposed_solution"));
        idea.setBusinessModel(rs.getString("business_model"));
        idea.setOverallScore(rs.getDouble("overall_score"));
        idea.setMarketScore(rs.getDouble("market_score"));
        idea.setFeasibilityScore(rs.getDouble("feasibility_score"));
        idea.setCompetitionScore(rs.getDouble("competition_score"));
        idea.setDepthScore(rs.getDouble("depth_score"));
        idea.setDecisionTier(DecisionTier.fromString(rs.getString("decision_tier")));

        String strStrengths = rs.getString("swot_strengths");
        if (strStrengths != null && !strStrengths.isEmpty()) {
            idea.setStrengths(Arrays.asList(strStrengths.split(";;")));
        }
        String strWeaknesses = rs.getString("swot_weaknesses");
        if (strWeaknesses != null && !strWeaknesses.isEmpty()) {
            idea.setWeaknesses(Arrays.asList(strWeaknesses.split(";;")));
        }
        String strOpportunities = rs.getString("swot_opportunities");
        if (strOpportunities != null && !strOpportunities.isEmpty()) {
            idea.setOpportunities(Arrays.asList(strOpportunities.split(";;")));
        }
        String strThreats = rs.getString("swot_threats");
        if (strThreats != null && !strThreats.isEmpty()) {
            idea.setThreats(Arrays.asList(strThreats.split(";;")));
        }
        String strRisks = rs.getString("risks");
        if (strRisks != null && !strRisks.isEmpty()) {
            idea.setRisks(Arrays.asList(strRisks.split(";;")));
        }

        idea.setCreatedAt(rs.getTimestamp("created_at"));
        return idea;
    }
}
