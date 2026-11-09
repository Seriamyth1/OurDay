import java.awt.Color;
import java.awt.Font;

public final class ThemeManager {
    public static final Color BACKGROUND_START = new Color(225, 232, 255);
    public static final Color BACKGROUND_END = new Color(255, 235, 245);
    public static final Color TEXT = new Color(44, 53, 78);
    public static final Color MUTED_TEXT = new Color(106, 116, 140);
    public static final Color ACCENT = new Color(97, 112, 205);
    public static final Color CELL = new Color(255, 255, 255, 235);
    public static final Color[] EVENT_COLORS = {
        new Color(103, 119, 219),
        new Color(226, 130, 159),
        new Color(79, 159, 143),
        new Color(224, 163, 83),
        new Color(147, 112, 190)
    };
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 13);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);

    private ThemeManager() {
    }

    public static Color paletteColor(String name) {
        switch (name) {
            case "White":
                return Color.WHITE;
            case "Rose":
                return new Color(255, 224, 235);
            case "Lavender":
                return new Color(232, 222, 255);
            case "Sky Blue":
                return new Color(218, 237, 255);
            case "Mint":
                return new Color(218, 246, 231);
            case "Peach":
                return new Color(255, 230, 210);
            default:
                throw new IllegalArgumentException("Unknown background palette: " + name);
        }
    }

    public static Color eventColor(String eventTitle) {
        int index = Math.floorMod(eventTitle.hashCode(), EVENT_COLORS.length);
        return EVENT_COLORS[index];
    }
}
