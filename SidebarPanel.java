import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {
    private final JLabel miniMonthLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel miniGrid = new JPanel(new GridLayout(0, 7, 2, 4));
    private final Consumer<LocalDate> onDateSelected;

    public SidebarPanel(Runnable onToday, Runnable onThisMonth, Consumer<LocalDate> onDateSelected) {
        super(new BorderLayout(0, 14));
        this.onDateSelected = onDateSelected;
        setOpaque(true);
        setBackground(new Color(255, 255, 255, 185));
        setBorder(new EmptyBorder(16, 12, 16, 12));
        setPreferredSize(new Dimension(215, 0));

        JLabel brand = new JLabel("OurDay");
        brand.setFont(new Font("SansSerif", Font.BOLD, 21));
        brand.setForeground(ThemeManager.TEXT);

        JPanel navigation = new JPanel(new GridLayout(2, 1, 0, 6));
        navigation.setOpaque(false);
        JButton today = new JButton("Today");
        today.addActionListener(event -> onToday.run());
        JButton thisMonth = new JButton("This month");
        thisMonth.addActionListener(event -> onThisMonth.run());
        navigation.add(today);
        navigation.add(thisMonth);

        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(brand, BorderLayout.NORTH);
        top.add(navigation, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel miniCalendar = new JPanel(new BorderLayout(0, 8));
        miniCalendar.setOpaque(false);
        miniMonthLabel.setFont(ThemeManager.SUBTITLE_FONT);
        miniMonthLabel.setForeground(ThemeManager.TEXT);
        miniCalendar.add(miniMonthLabel, BorderLayout.NORTH);
        miniGrid.setOpaque(false);
        miniCalendar.add(miniGrid, BorderLayout.CENTER);
        add(miniCalendar, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        JLabel markers = new JLabel("EVENT COLORS");
        markers.setFont(new Font("SansSerif", Font.BOLD, 10));
        markers.setForeground(ThemeManager.MUTED_TEXT);
        footer.add(markers);
        footer.add(Box.createVerticalStrut(8));
        for (int i = 0; i < ThemeManager.EVENT_COLORS.length; i++) {
            JLabel marker = new JLabel("Event color " + (i + 1));
            marker.setFont(new Font("SansSerif", Font.PLAIN, 11));
            marker.setIcon(new ColorDotIcon(ThemeManager.EVENT_COLORS[i]));
            marker.setIconTextGap(8);
            footer.add(marker);
            footer.add(Box.createVerticalStrut(5));
        }
        add(footer, BorderLayout.SOUTH);
        setMonth(YearMonth.now());
    }

    public void setMonth(YearMonth month) {
        miniMonthLabel.setText(month.getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault())
            + " " + month.getYear());
        miniGrid.removeAll();

        String[] weekdays = {"S", "M", "T", "W", "T", "F", "S"};
        for (String weekday : weekdays) {
            JLabel label = new JLabel(weekday, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 10));
            label.setForeground(ThemeManager.MUTED_TEXT);
            miniGrid.add(label);
        }

        int offset = month.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < offset; i++) {
            miniGrid.add(new JLabel(""));
        }
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            JButton dayButton = new JButton(Integer.toString(day));
            dayButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dayButton.setMargin(new Insets(2, 0, 2, 0));
            dayButton.setFocusPainted(false);
            dayButton.setBorderPainted(false);
            dayButton.setContentAreaFilled(date.equals(LocalDate.now()));
            if (date.equals(LocalDate.now())) {
                dayButton.setBackground(new Color(221, 226, 255));
                dayButton.setForeground(ThemeManager.ACCENT);
            }
            dayButton.setToolTipText("Open events for " + date);
            dayButton.addActionListener(event -> onDateSelected.accept(date));
            miniGrid.add(dayButton);
        }
        miniGrid.revalidate();
        miniGrid.repaint();
    }

    private static class ColorDotIcon implements Icon {
        private final Color color;

        private ColorDotIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            graphics.setColor(color);
            graphics.fillOval(x, y + 2, 9, 9);
        }

        @Override
        public int getIconWidth() {
            return 12;
        }

        @Override
        public int getIconHeight() {
            return 13;
        }
    }
}
