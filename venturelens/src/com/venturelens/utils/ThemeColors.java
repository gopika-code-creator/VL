package com.venturelens.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * VentureLens cohesive color system and typography constants.
 * Strictly adhering to:
 * - Base Background: #0B1320
 * - Cards & Panels: #162235
 * - Accent Color: #10B981 (Emerald)
 * - Primary Text: #F1F5F9
 * - Muted Subtitles: #94A3B8
 */
public final class ThemeColors {
    private ThemeColors() {}

    // Core Canvas & Surfaces
    public static final Color BASE_BG = new Color(0x0B, 0x13, 0x20);        // #0B1320
    public static final Color CARD_BG = new Color(0x16, 0x22, 0x35);        // #162235
    public static final Color CARD_BG_HOVER = new Color(0x1F, 0x2E, 0x47);  // Slightly elevated
    public static final Color INPUT_BG = new Color(0x0E, 0x18, 0x28);       // Deep input field bg
    public static final Color BORDER = new Color(0x27, 0x38, 0x52);         // Crisp panel divider
    public static final Color BORDER_FOCUS = new Color(0x10, 0xB9, 0x81);   // Accent glow

    // Accents & Actions
    public static final Color ACCENT = new Color(0x10, 0xB9, 0x81);          // #10B981 Emerald
    public static final Color ACCENT_HOVER = new Color(0x05, 0x96, 0x69);    // Darker emerald
    public static final Color ACCENT_DARK = new Color(0x06, 0x4E, 0x3B);     // Tint background
    public static final Color SECONDARY = new Color(0x3B, 0x82, 0xF6);       // Blue
    public static final Color DANGER = new Color(0xEF, 0x44, 0x44);          // Red

    // Typography Colors
    public static final Color TEXT_PRIMARY = new Color(0xF1, 0xF5, 0xF9);    // #F1F5F9
    public static final Color TEXT_MUTED = new Color(0x94, 0xA3, 0xB8);      // #94A3B8
    public static final Color TEXT_SUBTLE = new Color(0x64, 0x74, 0x8B);     // Darker slate

    // Decision Tiers
    public static final Color TIER_STRONG_GO = new Color(0x10, 0xB9, 0x81); // Emerald
    public static final Color TIER_GO = new Color(0x3B, 0x82, 0xF6);        // Blue
    public static final Color TIER_CAUTION = new Color(0xF5, 0x9E, 0x0B);   // Amber
    public static final Color TIER_PIVOT = new Color(0xEF, 0x44, 0x44);     // Rose/Red

    // Standard Clean Swing Fonts
    public static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font FONT_SECTION = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font(Font.MONOSPACED, Font.PLAIN, 12);
}
