import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class CalendarDayPanel extends JPanel {
    private final Color borderColor;

    public CalendarDayPanel(LocalDate date, List<String> events, boolean today, Consumer<LocalDate> onClick) {
        super(new BorderLayout(0, 7));
        borderColor = today ? ThemeManager.ACCENT : new Color(227, 230, 240);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 8, 10));
        setToolTipText(date + " — click to manage events");

        JLabel dayLabel = new JLabel(
            date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + "  " + date.getDayOfMonth());
        dayLabel.setFont(today ? ThemeManager.SUBTITLE_FONT : ThemeManager.BODY_FONT);
        dayLabel.setForeground(today ? ThemeManager.ACCENT : ThemeManager.TEXT);
        add(dayLabel, BorderLayout.NORTH);

        JPanel eventList = new JPanel();
        eventList.setOpaque(false);
        eventList.setLayout(new BoxLayout(eventList, BoxLayout.Y_AXIS));
        int visibleEvents = Math.min(events.size(), 3);
        for (int i = 0; i < visibleEvents; i++) {
            eventList.add(createEventMarker(events.get(i)));
            eventList.add(Box.createVerticalStrut(4));
        }
        if (events.size() > visibleEvents) {
            JLabel more = new JLabel("+" + (events.size() - visibleEvents) + " more");
            more.setFont(new Font("SansSerif", Font.PLAIN, 11));
            more.setForeground(ThemeManager.MUTED_TEXT);
            eventList.add(more);
        }
        add(eventList, BorderLayout.CENTER);

        MouseAdapter clickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                onClick.accept(date);
            }
        };
        installClickHandler(this, clickHandler);
    }

    private JLabel createEventMarker(String title) {
        Color eventColor = ThemeManager.eventColor(title);
        JLabel marker = new JLabel(title, SwingConstants.LEFT);
        marker.setFont(new Font("SansSerif", Font.PLAIN, 11));
        marker.setForeground(ThemeManager.TEXT);
        marker.setOpaque(true);
        marker.setBackground(new Color(eventColor.getRed(), eventColor.getGreen(), eventColor.getBlue(), 42));
        marker.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, eventColor),
            BorderFactory.createEmptyBorder(3, 5, 3, 4)));
        marker.setAlignmentX(LEFT_ALIGNMENT);
        marker.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return marker;
    }

    private void installClickHandler(Component component, MouseAdapter handler) {
        component.addMouseListener(handler);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                installClickHandler(child, handler);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.CELL);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(borderColor.equals(ThemeManager.ACCENT) ? 2f : 1f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.dispose();
    }
}
