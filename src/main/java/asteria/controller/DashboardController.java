package asteria.controller;

import asteria.model.WatchlistItem;
import asteria.services.orchestrator.AsteriaOrchestrator;
import asteria.services.watchlist.WatchlistService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    private final AsteriaOrchestrator orchestrator;
    private WatchlistService watchlistService;
    private int currentUserId;

    private final ObservableList<WatchlistItem> watchItems = FXCollections.observableArrayList();

    @FXML private VBox watchlistSection;
    @FXML private HBox watchlistBox;

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
    private void handleDownload(ActionEvent event) throws IOException, InterruptedException { }

    @FXML
    private void handleCalc(ActionEvent event) { }
}
