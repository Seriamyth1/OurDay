import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;

public class CalendarHeader extends JPanel {
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final Consumer<YearMonth> onMonthChanged;
    private YearMonth month;

    public CalendarHeader(YearMonth month, Consumer<YearMonth> onMonthChanged) {
        super(new BorderLayout(12, 0));
        this.onMonthChanged = onMonthChanged;
        setOpaque(false);

        JButton previous = navigationButton("‹", "Previous month");
        previous.addActionListener(e -> changeMonth(-1));
        JButton next = navigationButton("›", "Next month");
        next.addActionListener(e -> changeMonth(1));

        monthLabel.setFont(ThemeManager.TITLE_FONT);
        monthLabel.setForeground(ThemeManager.TEXT);
        add(previous, BorderLayout.WEST);
        add(monthLabel, BorderLayout.CENTER);
        add(next, BorderLayout.EAST);
        setMonth(month);
    }

    public void setMonth(YearMonth month) {
        this.month = month;
        monthLabel.setText(month.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
            + " " + month.getYear());
    }

    private void changeMonth(int amount) {
        onMonthChanged.accept(month.plusMonths(amount));
    }

    private JButton navigationButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 28));
        button.setForeground(ThemeManager.ACCENT);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        return button;
    }
}
