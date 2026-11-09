package com.ourday;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class OurDayApp extends Application {

    private YearMonth currentMonth = YearMonth.now();
    private final Label monthLabel = new Label();
    private final GridPane grid = new GridPane();

    @Override
    public void start(Stage stage) {
        Button prev = new Button("<");
        Button next = new Button(">");
        monthLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        prev.setOnAction(e -> { currentMonth = currentMonth.minusMonths(1); drawMonth(); });
        next.setOnAction(e -> { currentMonth = currentMonth.plusMonths(1); drawMonth(); });

        HBox topBar = new HBox(12, prev, monthLabel, next);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));

        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(grid);

        drawMonth();

        stage.setTitle("OurDay");
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    private void drawMonth() {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // 7 equal-width columns
        for (int c = 0; c < 7; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            grid.getColumnConstraints().add(cc);
        }

        // Weekday headers (Sunday first)
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int c = 0; c < 7; c++) {
            Label h = new Label(days[c]);
            h.setMaxWidth(Double.MAX_VALUE);
            h.setAlignment(Pos.CENTER);
            grid.add(h, c, 0);
        }

        // Day cells
        int offset = currentMonth.atDay(1).getDayOfWeek().getValue() % 7; // Sunday = 0
        int length = currentMonth.lengthOfMonth();
        for (int d = 1; d <= length; d++) {
            int index = offset + d - 1;
            int col = index % 7;
            int row = index / 7 + 1;

            Label dayNum = new Label(String.valueOf(d));
            VBox cell = new VBox(dayNum);
            cell.setPadding(new Insets(6));
            cell.setMinHeight(90);
            cell.setStyle("-fx-border-color: #cccccc; -fx-background-color: white;");
            GridPane.setHgrow(cell, Priority.ALWAYS);
            grid.add(cell, col, row);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}