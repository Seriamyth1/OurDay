import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class EventManager {
    private final File eventFile;
    private final Map<LocalDate, List<String>> events = new HashMap<>();

    public EventManager(File eventFile) {
        this.eventFile = eventFile;
    }

    public List<String> getEvents(LocalDate date) {
        List<String> dayEvents = events.get(date);
        return dayEvents == null ? Collections.emptyList() : Collections.unmodifiableList(dayEvents);
    }

    public void addEvent(LocalDate date, String title) {
        events.computeIfAbsent(date, ignored -> new ArrayList<>()).add(title);
        save();
    }

    public void updateEvent(LocalDate date, int index, String title) {
        List<String> dayEvents = requireEvents(date);
        dayEvents.set(index, title);
        save();
    }

    public void removeEvent(LocalDate date, int index) {
        List<String> dayEvents = requireEvents(date);
        dayEvents.remove(index);
        if (dayEvents.isEmpty()) {
            events.remove(date);
        }
        save();
    }

    public void load() {
        if (!eventFile.exists()) {
            return;
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(eventFile))) {
            Object loaded = input.readObject();
            if (!(loaded instanceof Map<?, ?>)) {
                throw new IOException("Saved events file has an invalid format.");
            }
            Map<?, ?> loadedEvents = (Map<?, ?>) loaded;
            for (Map.Entry<?, ?> entry : loadedEvents.entrySet()) {
                if (!(entry.getKey() instanceof LocalDate) || !(entry.getValue() instanceof List<?>)) {
                    throw new IOException("Saved events file contains invalid entries.");
                }
                List<String> titles = new ArrayList<>();
                for (Object title : (List<?>) entry.getValue()) {
                    if (!(title instanceof String)) {
                        throw new IOException("Saved events file contains an invalid event.");
                    }
                    titles.add((String) title);
                }
                events.put((LocalDate) entry.getKey(), titles);
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                "Could not load saved OurDay events: " + ex.getMessage(),
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String> requireEvents(LocalDate date) {
        List<String> dayEvents = events.get(date);
        if (dayEvents == null) {
            throw new IllegalArgumentException("No events exist for " + date);
        }
        return dayEvents;
    }

    private void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(eventFile))) {
            output.writeObject(new HashMap<>(events));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Could not save events: " + ex.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
