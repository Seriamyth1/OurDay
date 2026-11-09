import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public final class EventDialog {
    private EventDialog() {
    }

    public static void show(Component owner, LocalDate date, EventManager manager, Runnable onUpdated) {
        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(owner),
            "Events for " + date,
            Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        DefaultListModel<String> model = new DefaultListModel<>();
        reload(model, manager.getEvents(date));
        JList<String> eventList = new JList<>(model);
        eventList.setFont(ThemeManager.BODY_FONT);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.setVisibleRowCount(8);
        dialog.add(new JScrollPane(eventList), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton remove = new JButton("Remove");
        JButton close = new JButton("Done");
        buttons.add(add);
        buttons.add(edit);
        buttons.add(remove);
        buttons.add(close);
        dialog.add(buttons, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            reload(model, manager.getEvents(date));
            onUpdated.run();
        };
        add.addActionListener(event -> {
            String title = promptTitle(dialog, "Add event", "");
            if (title != null) {
                manager.addEvent(date, title);
                refresh.run();
            }
        });
        edit.addActionListener(event -> {
            int index = eventList.getSelectedIndex();
            if (index < 0) {
                showSelectionRequired(dialog);
                return;
            }
            String title = promptTitle(dialog, "Edit event", model.get(index));
            if (title != null) {
                manager.updateEvent(date, index, title);
                refresh.run();
                eventList.setSelectedIndex(index);
            }
        });
        remove.addActionListener(event -> {
            int index = eventList.getSelectedIndex();
            if (index < 0) {
                showSelectionRequired(dialog);
                return;
            }
            manager.removeEvent(date, index);
            refresh.run();
        });
        close.addActionListener(event -> dialog.dispose());

        dialog.setMinimumSize(new Dimension(390, 320));
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private static String promptTitle(Component parent, String title, String initialValue) {
        String value = (String) JOptionPane.showInputDialog(
            parent,
            "Event title:",
            title,
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            initialValue
        );
        if (value == null) {
            return null;
        }
        if (value.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Event titles cannot be empty.", "Invalid Event", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return value.trim();
    }

    private static void reload(DefaultListModel<String> model, java.util.List<String> events) {
        model.clear();
        for (String event : events) {
            model.addElement(event);
        }
    }

    private static void showSelectionRequired(Component parent) {
        JOptionPane.showMessageDialog(parent, "Select an event first.", "No Event Selected", JOptionPane.INFORMATION_MESSAGE);
    }
}
