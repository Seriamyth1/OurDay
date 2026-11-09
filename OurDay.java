import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.*;

public class OurDay {
    private JFrame frame;
    private CustomizableBackgroundPanel background;
    private CalendarHeader calendarHeader;
    private SidebarPanel sidebar;
    private JPanel calendarGrid;
    private YearMonth displayMonth = YearMonth.now();
    private final EventManager eventManager = new EventManager(new File("ourday-events.dat"));

    private static final String[] WEEKDAYS = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OurDay().createAndShowGUI());
    }

    private void createAndShowGUI() {
        eventManager.load();

        frame = new JFrame("Ourday | Calendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(900, 650));
        frame.setSize(1180, 780);
        frame.setLocationRelativeTo(null);

        background = new CustomizableBackgroundPanel();
        background.setGradient(ThemeManager.BACKGROUND_START, ThemeManager.BACKGROUND_END);
        background.setLayout(new BorderLayout(18, 14));
        background.setBorder(new EmptyBorder(18, 18, 18, 18));

        sidebar = new SidebarPanel(
            this::showToday,
            this::showCurrentMonth,
            this::showSelectedDate
        );
        background.add(sidebar, BorderLayout.WEST);
        background.add(createMainContent(), BorderLayout.CENTER);

        frame.setContentPane(background);
        frame.setVisible(true);
        refreshCalendar();
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(12, 14));
        content.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(8, 12));
        header.setOpaque(false);
        calendarHeader = new CalendarHeader(displayMonth, month -> {
            displayMonth = month;
            refreshCalendar();
        });
        header.add(calendarHeader, BorderLayout.NORTH);
        header.add(createWeekdayHeader(), BorderLayout.CENTER);
        header.add(createCustomizationBar(), BorderLayout.SOUTH);
        content.add(header, BorderLayout.NORTH);

        calendarGrid = new JPanel(new GridLayout(6, 7, 9, 9));
        calendarGrid.setOpaque(false);
        content.add(calendarGrid, BorderLayout.CENTER);
        return content;
    }

    private JPanel createWeekdayHeader() {
        JPanel weekdays = new JPanel(new GridLayout(1, 7, 9, 0));
        weekdays.setOpaque(false);
        for (String weekday : WEEKDAYS) {
            JLabel label = new JLabel(weekday, SwingConstants.CENTER);
            label.setFont(ThemeManager.SUBTITLE_FONT);
            label.setForeground(ThemeManager.MUTED_TEXT);
            weekdays.add(label);
        }
        return weekdays;
    }

    private JPanel createCustomizationBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 6));
        bar.setOpaque(false);

        JLabel customize = new JLabel("Personalize");
        customize.setFont(ThemeManager.SUBTITLE_FONT);
        customize.setForeground(ThemeManager.TEXT);
        bar.add(customize);

        String[] palette = {"White", "Rose", "Lavender", "Sky Blue", "Mint", "Peach"};
        JComboBox<String> colorChoice = new JComboBox<>(palette);
        colorChoice.setToolTipText("Choose a solid background");
        colorChoice.addActionListener(e -> applyPalette((String) colorChoice.getSelectedItem()));
        bar.add(colorChoice);

        JButton gradient = new JButton("Gradient");
        gradient.addActionListener(e -> background.setGradient(
            ThemeManager.BACKGROUND_START, ThemeManager.BACKGROUND_END));
        bar.add(gradient);

        JButton chooseImage = new JButton("Choose Image");
        chooseImage.addActionListener(e -> chooseBackgroundImage());
        bar.add(chooseImage);

        JSlider opacity = new JSlider(0, 100, 100);
        opacity.setPreferredSize(new Dimension(110, 30));
        opacity.setOpaque(false);
        opacity.setToolTipText("Adjust image transparency");
        opacity.addChangeListener(e -> background.setImageOpacity(opacity.getValue() / 100f));
        bar.add(opacity);

        JButton reset = new JButton("Reset");
        reset.addActionListener(e -> {
            background.setGradient(ThemeManager.BACKGROUND_START, ThemeManager.BACKGROUND_END);
            opacity.setValue(100);
            colorChoice.setSelectedIndex(0);
        });
        bar.add(reset);
        return bar;
    }

    private void applyPalette(String name) {
        if (name == null) {
            return;
        }

        background.setSolidColor(ThemeManager.paletteColor(name));
    }

    private void chooseBackgroundImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose an OurDay background image");
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif", "bmp"));
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                background.setBackgroundImage(chooser.getSelectedFile().getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Image Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshCalendar() {
        if (calendarGrid == null) {
            return;
        }

        calendarGrid.removeAll();

        LocalDate first = displayMonth.atDay(1);
        int offset = first.getDayOfWeek().getValue() % 7;
        int daysInMonth = displayMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int cell = 0; cell < 42; cell++) {
            int dayNumber = cell - offset + 1;

            if (dayNumber < 1 || dayNumber > daysInMonth) {
                JPanel blank = new JPanel();
                blank.setOpaque(false);
                calendarGrid.add(blank);
                continue;
            }

            LocalDate date = displayMonth.atDay(dayNumber);
            calendarGrid.add(new CalendarDayPanel(
                date,
                eventManager.getEvents(date),
                date.equals(today),
                this::manageEventsForDate
            ));
        }

        calendarHeader.setMonth(displayMonth);
        sidebar.setMonth(displayMonth);
        calendarGrid.revalidate();
        calendarGrid.repaint();
    }

    private void manageEventsForDate(LocalDate date) {
        EventDialog.show(frame, date, eventManager, this::refreshCalendar);
    }

    private void showToday() {
        displayMonth = YearMonth.now();
        refreshCalendar();
        manageEventsForDate(LocalDate.now());
    }

    private void showCurrentMonth() {
        displayMonth = YearMonth.now();
        refreshCalendar();
    }

    private void showSelectedDate(LocalDate date) {
        displayMonth = YearMonth.from(date);
        refreshCalendar();
        manageEventsForDate(date);
    }
}
