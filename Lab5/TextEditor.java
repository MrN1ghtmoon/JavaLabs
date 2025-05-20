package org.example.Lab5;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Optional;

public class TextEditor extends Application {
    private TextArea textArea = new TextArea();
    private Label lineNumbers = new Label();
    private String currentFilePath = null;
    private Connection connection;
    private boolean autoSaveEnabled = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        connectToDatabase();
        createTables();

        // Line numbers styling
        lineNumbers.setPadding(new Insets(10, 10, 10, 10));
        lineNumbers.setStyle("-fx-background-color: #e31616; -fx-border-color: #1ad90d;");
        lineNumbers.setFont(Font.font("Monospaced", 14));

        setupLineNumberUpdater();

        // Menu
        MenuBar menuBar = createMenuBar(primaryStage);

        // Toolbar
        ToolBar toolBar = createToolBar(primaryStage);

        // Status Bar
        HBox statusBar = createStatusBar();

        // Content Box with Line Numbers + Text Area
        HBox contentBox = new HBox(lineNumbers, textArea);
        HBox.setHgrow(textArea, Priority.ALWAYS);

        // Main Layout
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(contentBox);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 400, 425);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Text Editor");
        primaryStage.show();

        // Feature Setup
        setupWordCount(statusBar);
        setupAutoSave(statusBar);
        setupCharacterBasedLineWrap();
        textArea.setWrapText(false); // Default state
    }

    // --- MENU BAR ---
    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newFileItem = new MenuItem("New");
        newFileItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newFileItem.setOnAction(e -> newFile());

        MenuItem openItem = new MenuItem("Open");
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openItem.setOnAction(e -> openFile(primaryStage));

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveItem.setOnAction(e -> saveFile(primaryStage));

        MenuItem saveAsItem = new MenuItem("Save as...");
        saveAsItem.setOnAction(e -> saveFileAs(primaryStage));

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> exitApp());

        fileMenu.getItems().addAll(newFileItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exitItem);

        // Edit Menu
        Menu editMenu = new Menu("Сorrection");
        MenuItem findItem = new MenuItem("Find");
        findItem.setOnAction(e -> showFindWindow(primaryStage));

        MenuItem replaceItem = new MenuItem("Replace");
        replaceItem.setOnAction(e -> showReplaceWindow(primaryStage));

        MenuItem historyItem = new MenuItem("History");
        historyItem.setOnAction(e -> showHistoryWindow(primaryStage));

        editMenu.getItems().addAll(findItem, replaceItem, new SeparatorMenuItem(), historyItem);

        // Settings Menu
        Menu settingsMenu = new Menu("Settings");

        CheckMenuItem wordWrapItem = new CheckMenuItem("Hyphenation of words");
        wordWrapItem.setOnAction(e -> textArea.setWrapText(wordWrapItem.isSelected()));

        CheckMenuItem autoSaveItem = new CheckMenuItem("Auto-save");
        autoSaveItem.setSelected(autoSaveEnabled);
        autoSaveItem.setOnAction(e -> autoSaveEnabled = autoSaveItem.isSelected());

        settingsMenu.getItems().addAll(wordWrapItem, autoSaveItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, settingsMenu);
        return menuBar;
    }

    // --- TOOLBAR ---
    private ToolBar createToolBar(Stage primaryStage) {
        ToolBar toolBar = new ToolBar();
        Button newBtn = new Button("New");
        newBtn.setOnAction(e -> newFile());

        Button openBtn = new Button("Open");
        openBtn.setOnAction(e -> openFile(primaryStage));

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> saveFile(primaryStage));

        toolBar.getItems().addAll(newBtn, openBtn, saveBtn);
        return toolBar;
    }

    // --- STATUS BAR ---
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5));
        Label statusLabel = new Label("Ready");
        Label wordCountLabel = new Label("Слов: 0 | Символов: 0");

        statusBar.getChildren().addAll(statusLabel, wordCountLabel);
        statusBar.getProperties().put("statusLabel", statusLabel);
        statusBar.getProperties().put("wordCountLabel", wordCountLabel);

        return statusBar;
    }

    // --- WORD COUNT ---
    private void setupWordCount(HBox statusBar) {
        Label wordCountLabel = (Label) statusBar.getProperties().get("wordCountLabel");

        textArea.textProperty().addListener((obs, oldText, newText) -> {
            int words = newText.trim().isEmpty() ? 0 : newText.trim().split("\\s+").length;
            int chars = newText.length();
            wordCountLabel.setText("Слов: " + words + " | Символов: " + chars);
        });
    }

    // --- AUTO SAVE ---
    private void setupAutoSave(HBox statusBar) {
        Label statusLabel = (Label) statusBar.getProperties().get("statusLabel");

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (autoSaveEnabled && currentFilePath != null && !textArea.getText().trim().isEmpty()) {
                try {
                    Files.write(Paths.get(currentFilePath), textArea.getText().getBytes());
                    saveToDatabase(currentFilePath, textArea.getText());
                    statusLabel.setText("Автосохранение выполнено");
                    new Timeline(new KeyFrame(Duration.seconds(3), ev -> statusLabel.setText("Готов"))).play();
                } catch (IOException e) {
                    statusLabel.setText("Ошибка автосохранения");
                    new Timeline(new KeyFrame(Duration.seconds(3), ev -> statusLabel.setText("Готов"))).play();
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // --- LINE NUMBERS ---
    private void setupLineNumberUpdater() {
        textArea.textProperty().addListener((obs, oldText, newText) -> updateLineNumbers());
        textArea.scrollTopProperty().addListener((obs, oldVal, newVal) -> updateScrollSync());
        updateLineNumbers();
    }

    private void updateLineNumbers() {
        int lines = textArea.getText().split("\n").length;
        StringBuilder lineNums = new StringBuilder();
        for (int i = 1; i <= lines; i++) {
            lineNums.append(i).append("\n");
        }
        lineNumbers.setText(lineNums.toString());
    }

    private void updateScrollSync() {
        double scrollPos = textArea.getScrollTop();
        lineNumbers.setTranslateY(-scrollPos);
    }

    // --- CHARACTER-BASED WRAP EVERY 50 CHARACTERS ---
    private void setupCharacterBasedLineWrap() {
        textArea.textProperty().addListener((obs, oldText, newText) -> {
            if (!textArea.isFocused()) return;

            int caretPosition = textArea.getCaretPosition();

            String[] lines = newText.split("\n");
            StringBuilder result = new StringBuilder();

            for (String line : lines) {
                for (int i = 0; i < line.length(); i += 50) {
                    int end = Math.min(i + 50, line.length());
                    result.append(line.substring(i, end));
                    if (end < line.length()) result.append("\n");
                }
                result.append("\n");
            }

            if (result.length() > 0 && result.charAt(result.length() - 1) == '\n') {
                result.deleteCharAt(result.length() - 1);
            }

            textArea.textProperty().removeListener(this::handleTextChange);
            textArea.setText(result.toString());
            textArea.textProperty().addListener(this::handleTextChange);

            int newCaretPos = Math.min(caretPosition + ((result.length() - oldText.length())), result.length());
            textArea.positionCaret(newCaretPos);
        });
    }

    private void handleTextChange(ObservableValue<? extends String> obs, String oldText, String newText) {
        // For recursion control
    }

    // --- DATABASE CONNECTION ---
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:text_editor.db");
        } catch (SQLException e) {
            showError("Ошибка подключения к БД", e.getMessage());
        }
    }

    private void createTables() {
        String createFilesTable = "CREATE TABLE IF NOT EXISTS files (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "filename TEXT NOT NULL, " +
                "content TEXT NOT NULL, " +
                "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createFilesTable);
        } catch (SQLException e) {
            showError("Ошибка создания таблицы", e.getMessage());
        }
    }

    // --- FILE OPERATIONS ---
    private void newFile() {
        if (confirmUnsavedChanges()) {
            textArea.clear();
            currentFilePath = null;
        }
    }

    private void openFile(Stage primaryStage) {
        if (!confirmUnsavedChanges()) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                textArea.setText(content);
                currentFilePath = selectedFile.getAbsolutePath();
            } catch (IOException e) {
                showError("Ошибка открытия файла", e.getMessage());
            }
        }
    }

    private void saveFile(Stage primaryStage) {
        if (currentFilePath == null) {
            saveFileAs(primaryStage);
        } else {
            try {
                Files.write(Paths.get(currentFilePath), textArea.getText().getBytes());
                saveToDatabase(currentFilePath, textArea.getText());
            } catch (IOException e) {
                showError("Ошибка сохранения файла", e.getMessage());
            }
        }
    }

    private void saveFileAs(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            try {
                Files.write(selectedFile.toPath(), textArea.getText().getBytes());
                currentFilePath = selectedFile.getAbsolutePath();
                saveToDatabase(currentFilePath, textArea.getText());
            } catch (IOException e) {
                showError("Ошибка сохранения файла", e.getMessage());
            }
        }
    }

    private void saveToDatabase(String filename, String content) {
        String sql = "INSERT INTO files(filename, content) VALUES(?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, filename);
            ps.setString(2, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            showError("Ошибка сохранения в БД", e.getMessage());
        }
    }

    // --- UNSAVED CHANGES CONFIRMATION ---
    private boolean confirmUnsavedChanges() {
        if (!textArea.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Несохраненные изменения");
            alert.setHeaderText("У вас есть несохраненные изменения");
            alert.setContentText("Вы хотите сохранить их перед продолжением?");
            ButtonType buttonYes = new ButtonType("Да");
            ButtonType buttonNo = new ButtonType("Нет");
            ButtonType buttonCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);
            return alert.showAndWait().orElse(buttonCancel) != buttonCancel;
        }
        return true;
    }

    private void exitApp() {
        if (confirmUnsavedChanges()) {
            System.exit(0);
        }
    }

    // --- FIND / REPLACE / HISTORY ---
    private void showFindWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Найти");
        dialog.initOwner(primaryStage);
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        TextField searchField = new TextField();
        Button findBtn = new Button("Найти");
        findBtn.setOnAction(e -> {
            String text = textArea.getText();
            String searchText = searchField.getText();
            int index = text.indexOf(searchText);
            if (index != -1) {
                textArea.selectPositionCaret(index);
                textArea.extendSelection(index + searchText.length());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Не найдено");
                alert.setContentText("Текст не найден");
                alert.showAndWait();
            }
            dialog.close();
        });
        vbox.getChildren().addAll(new Label("Что найти:"), searchField, findBtn);
        Scene scene = new Scene(vbox, 300, 100);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showReplaceWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("Заменить");
        dialog.initOwner(primaryStage);
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        TextField searchField = new TextField();
        TextField replaceField = new TextField();
        Button replaceBtn = new Button("Заменить");
        replaceBtn.setOnAction(e -> {
            String currentText = textArea.getText();
            String searchText = searchField.getText();
            String replaceText = replaceField.getText();
            String newText = currentText.replace(searchText, replaceText);
            textArea.setText(newText);
            dialog.close();
        });
        vbox.getChildren().addAll(
                new Label("Что найти:"), searchField,
                new Label("На что заменить:"), replaceField,
                replaceBtn
        );
        Scene scene = new Scene(vbox, 300, 150);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showHistoryWindow(Stage primaryStage) {
        Stage dialog = new Stage();
        dialog.setTitle("История файлов");
        dialog.initOwner(primaryStage);
        ListView<String> historyList = new ListView<>();
        loadHistory(historyList);
        Button openBtn = new Button("Открыть");
        openBtn.setOnAction(e -> {
            String selected = historyList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String content = getHistoryContent(selected);
                if (content != null) {
                    textArea.setText(content);
                    currentFilePath = selected;
                }
                dialog.close();
            }
        });
        VBox vbox = new VBox(10, historyList, openBtn);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private void loadHistory(ListView<String> historyList) {
        String sql = "SELECT filename FROM files GROUP BY filename ORDER BY MAX(last_modified) DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                historyList.getItems().add(rs.getString("filename"));
            }
        } catch (SQLException e) {
            showError("Ошибка загрузки истории", e.getMessage());
        }
    }

    private String getHistoryContent(String filename) {
        String sql = "SELECT content FROM files WHERE filename = ? ORDER BY last_modified DESC LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filename);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("content");
            }
        } catch (SQLException e) {
            showError("Ошибка загрузки содержимого", e.getMessage());
        }
        return null;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}