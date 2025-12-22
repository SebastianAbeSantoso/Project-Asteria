package asteria.controller;

import asteria.model.MarketInsight;
import asteria.model.MarketSnapshot;
import asteria.model.MarketView;
import asteria.model.WatchlistItem;
import asteria.services.insight.InsightRulesImpl;
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
    @FXML private Label analysisTitle;
    @FXML private Label trendValue;
    @FXML private Label volValue;
    @FXML private Label marketMoodPill;
    @FXML private Label watchlistCount;
    @FXML private Label aiSuggestValue;

    private final ObservableList<WatchlistItem> watchItems = FXCollections.observableArrayList();
    public DashboardController(AsteriaOrchestrator orchestrator, WatchlistService watchlistService) {
        this.orchestrator = orchestrator;
        this.watchlistService = watchlistService;
    }

    @FXML
    private void initialize() {watchlistSection.visibleProperty().bind(javafx.beans.binding.Bindings.isNotEmpty(watchItems));
        watchlistSection.managedProperty().bind(watchlistSection.visibleProperty());
        watchItems.addListener((javafx.collections.ListChangeListener<WatchlistItem>) c -> renderWatchlist()
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
                items -> Platform.runLater(() -> {
                    watchItems.setAll(items);

                    if (symbol == null && !items.isEmpty()) {
                        symbol = items.getFirst().getSymbol();
                        updateChatSymbolIndicator();
                        refreshAnalysis(symbol);
                    }
                }),
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
            symbol = item.getSymbol();
            updateChatSymbolIndicator();
            refreshAnalysis(symbol);
        });

        chip.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleRemoveSymbol(item.getSymbol());
            } else {
                symbol = item.getSymbol();
                updateChatSymbolIndicator();
                refreshAnalysis(symbol);
            }
        });

        return chip;
    }

    private void handleRemoveSymbol(String removedSymbol) {
        if (watchlistService == null) return;

        watchItems.removeIf(w -> w.getSymbol().equalsIgnoreCase(removedSymbol));

        watchlistService.removeFromWatchlist(currentUserId, removedSymbol, () -> {
                    appendChat("Asteria✦", " Removed " + removedSymbol + " from your watchlist.");

                    if (symbol != null && symbol.equalsIgnoreCase(removedSymbol)) {
                        String next = watchItems.isEmpty() ? null : watchItems.getFirst().getSymbol();
                        symbol = next;
                        updateChatSymbolIndicator();

                        if (symbol != null) refreshAnalysis(symbol);
                        else showAnalysisError();
                    }

                    refreshWatchlist();
                },
                err -> {
                    err.printStackTrace();
                    appendChat("Asteria ✦", "✦ Failed to remove " + removedSymbol + ".");
                    refreshWatchlist();
                }
        );
    }


    @FXML
    private void handleSend(ActionEvent event) {
        String msg = chatInput.getText();
        if (msg == null || msg.isBlank()) return;

        appendChat("You", msg);
        chatInput.clear();

        String aiMessage = orchestrator.getOpenAiResponse(msg, symbol);

        appendChat("Asteria✦ ", "\"" + aiMessage + "\"");
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

        appendChat("Asteria ✦", " Consulting the stars for " + symbolInput + "…");

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
                        },
                        err -> {
                            err.printStackTrace();
                            appendChat("Asteria",
                                    "✦ Failed to add " + symbolInput + " to your watchlist.");
                        }
                );
                symbol = symbolInput;
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            appendChat("Asteria",
                    "✦ " + symbolInput + " is now written among the stars.");
            updateChatSymbolIndicator();
            refreshWatchlist();
            refreshAnalysis(symbolInput);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();

            if (ex instanceof IllegalArgumentException) {
                appendChat("Asteria",
                        "✦ I cannot find " + symbolInput + " in the astral records.");
            } else {
                appendChat("Asteria",
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

    @FXML
    private void handleUpdateSymbol(ActionEvent event) {
        String ctx = (symbol == null) ? null : symbol.trim().toUpperCase();
        if (ctx == null || ctx.isBlank()) return;

        appendChat("Asteria", "✦ Updating " + ctx + "…");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                orchestrator.insertCsv(ctx);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            appendChat("Asteria", "✦ " + ctx + " updated successfully.");
            refreshWatchlist();
            refreshAnalysis(ctx);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            appendChat("Asteria", "✦ Update failed for " + ctx + ".");
            if (ex != null) ex.printStackTrace();
        });

        new Thread(task, "update-" + ctx).start();
    }



    private void applyInsight(MarketInsight i) {
        switch (i.trend()) {
            case UP -> trendValue.setText("Gentle upward");
            case DOWN -> trendValue.setText("Leaning downward");
            case SIDEWAYS -> trendValue.setText("Sideways drift");
        }

        switch (i.volatility()) {
            case LOW -> volValue.setText("Low and calm");
            case MEDIUM -> volValue.setText("Moderate waves");
            case HIGH -> volValue.setText("High turbulence");
        }

        applyMomentumPill(i.momentum());
    }

    private void applySnapshot(MarketSnapshot s) {
        analysisTitle.setText(s.symbol() + " ANALYSIS");
        aiSuggestValue.setText("Review " + s.symbol());
        watchlistCount.setText(watchItems.size() + " symbols");

    }

        private void refreshAnalysis(String symbol) {
        Task<MarketView> task = new Task<>() {
            @Override
            protected MarketView call() throws Exception {
                return orchestrator.getMarketView(symbol);
            }
        };

            task.setOnSucceeded(e -> {
                MarketView view = task.getValue();
                if (view == null || view.snapshot() == null || view.insight() == null) {
                    showAnalysisError();
                    return;
                }
                applyInsight(view.insight());
                applySnapshot(view.snapshot());
            });

        task.setOnFailed(e -> showAnalysisError());

        new Thread(task, "analysis-" + symbol).start();
    }

    private void applyMomentumPill(InsightRulesImpl.Momentum m) {
        switch (m) {
            case BULLISH -> marketMoodPill.setText("Uptrend");
            case BEARISH -> marketMoodPill.setText("Downtrend");
            case NEUTRAL -> marketMoodPill.setText("Neutral");
        }
    }

    private void showAnalysisError() {
        analysisTitle.setText("ANALYSIS");
        trendValue.setText("Unavailable");
        volValue.setText("Unavailable");
        marketMoodPill.setText("Unknown");
        aiSuggestValue.setText("Review");
    }
}
