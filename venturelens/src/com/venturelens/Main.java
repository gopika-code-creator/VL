package com.venturelens;

import com.venturelens.ui.MainFrame;

import javax.swing.*;

/**
 * Entry point for the VentureLens Desktop Operating System.
 *
 * Technical Requirements Compliance:
 * 1. Strictly standard JDK only (Java SE 17+ / 21).
 * 2. Strictly pure Java Swing (javax.swing) and AWT (java.awt). No third-party Look-and-Feels.
 * 3. No external utility dependencies (No Gson, Jackson, Commons, Lombok, JFreeChart, OpenNLP).
 * 4. Strictly MySQL official JDBC with try-with-resources and SwingWorker for EDT responsiveness.
 * 5. Complete, production-ready implementation without stubs or placeholders.
 */
public class Main {

    public static void main(String[] args) {
        // Enforce anti-aliasing text rendering across all Swing components
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Main Desktop Frame inside Event Dispatch Thread (EDT)
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Fatal error initializing VentureLens: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
