package asteria.controller;

import asteria.model.WatchlistItem;
import asteria.services.orchestrator.AsteriaOrchestrator;
import asteria.services.watchlist.WatchlistService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class DashboardController {

    private final AsteriaOrchestrator orchestrator;
    private WatchlistService watchlistService;
    private int currentUserId;
    private String symbol = null;
    @FXML private TextArea chatHistory;
    @FXML private TextField chatInput;
    @FXML private VBox watchlistSection;
    @FXML private HBox watchlistBox;
    @FXML private Label chatSymbolIndicator;

    private final ObservableList<WatchlistItem> watchItems = FXCollections.observableArrayList();
    public DashboardController(AsteriaOrchestrator orchestrator, WatchlistService watchlistService) {
        this.orchestrator = orchestrator;
        this.watchlistService = watchlistService;
    }

    @FXML
    private void initialize() {
        watchlistSection.visibleProperty()
                .bind(javafx.beans.binding.Bindings.isNotEmpty(watchItems));

        watchlistSection.managedProperty()
                .bind(watchlistSection.visibleProperty());

        watchItems.addListener(
                (javafx.collections.ListChangeListener<WatchlistItem>) c -> renderWatchlist()
        );
    }

    public void setContext(WatchlistService service, int userId) {
        this.watchlistService = service;
        this.currentUserId = userId;
        refreshWatchlist();
    }

    private void refreshWatchlist() {
        if (watchlistService == null) return;

        watchlistService.refreshWatchlist(
                currentUserId,
                items -> Platform.runLater(() -> watchItems.setAll(items)),
                err -> err.printStackTrace()
        );
    }

    private void renderWatchlist() {
        if (watchlistBox == null) return;

        watchlistBox.getChildren().clear();

        for (WatchlistItem item : watchItems) {
            VBox chip = buildChip(item);
            watchlistBox.getChildren().add(chip);
        }
    }

    private VBox buildChip(WatchlistItem item) {
        VBox chip = new VBox(4);
        chip.getStyleClass().add("chip");
        chip.setUserData(item.getSymbol());
        chip.setAlignment(Pos.CENTER);

        Label sym = new Label(item.getSymbol());
        sym.getStyleClass().add("chip-symbol");

        Label price = new Label(item.getFormattedPrice());
        price.getStyleClass().add("chip-price");

        Label chg = new Label(item.getFormattedChangePct());
        chg.getStyleClass().add(item.getChangePct() >= 0 ? "chip-up" : "chip-down");

        HBox row = new HBox(10, price, chg);
        row.setAlignment(Pos.CENTER);

        chip.getChildren().addAll(sym, row);

        chip.setOnMouseClicked(e -> {
        });

        return chip;
    }

    @FXML
    private void handleInput(ActionEvent event) throws SQLException, IOException {

    }

    @FXML
    private void handleImport(ActionEvent event) {

    }

    @FXML
    private void handleSend(ActionEvent event) {
        String msg = chatInput.getText();
        if (msg == null || msg.isBlank()) return;

        appendChat("You", msg);
        chatInput.clear();

        String aiMessage = orchestrator.getOpenAiResponse(msg, symbol);

        appendChat("Asteria ✦, ", " the star heard: \"" + aiMessage + "\"");
    }

    private void appendChat(String who, String text) {
        if (chatHistory.getText() == null || chatHistory.getText().isEmpty()) {
            chatHistory.setText(who + ": " + text);
        } else {
            chatHistory.appendText("\n\n" + who + ": " + text);
        }
        chatHistory.positionCaret(chatHistory.getLength());
    }

    @FXML
    private void handleSetChatSymbol(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Symbol");
        dialog.setHeaderText("Enter a stock symbol");
        dialog.setContentText("Symbol:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String symbolInput = result.get().trim().toUpperCase();
        if (symbolInput.isBlank()) return;

        appendChat("Asteria ✦", ", Consulting the stars for " + symbolInput + "…");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (!orchestrator.symbolExistsInDb(symbolInput)) {

                    if (!orchestrator.symbolExistsOnYahoo(symbolInput)) {
                        throw new IllegalArgumentException("Symbol not found");
                    }

                    orchestrator.insertCsv(symbolInput);
                }

                watchlistService.addToWatchlist(
                        currentUserId,
                        symbolInput,
                        null,
                        () -> {
                            symbol = symbolInput;
                            updateChatSymbolIndicator();
                            refreshWatchlist();
                            appendChat("Asteria ✦",
                                    "✦ " + symbolInput + " has been added to your watchlist.");
                        },
                        err -> {
                            err.printStackTrace();
                            appendChat("Asteria ✦",
                                    "✦ Failed to add " + symbolInput + " to your watchlist.");
                        }
                );

                symbol = symbolInput;

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            appendChat("Asteria ✦",
                    "✦ " + symbolInput + " is now written among the stars.");
            updateChatSymbolIndicator();
            refreshWatchlist();
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();

            if (ex instanceof IllegalArgumentException) {
                appendChat("Asteria ✦",
                        "✦ I cannot find " + symbolInput + " in the astral records.");
            } else {
                appendChat("Asteria ✦",
                        "✦ A disturbance occurred while reading " + symbolInput + ".");
                ex.printStackTrace();
            }
        });

        new Thread(task, "symbol-setup").start();
    }



    private void updateChatSymbolIndicator() {
        if (chatSymbolIndicator == null) return;

        chatSymbolIndicator.setText(symbol == null ? "Context: none" : "Context: " + symbol);
    }
}
