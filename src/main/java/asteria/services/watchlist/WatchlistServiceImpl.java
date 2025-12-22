package asteria.services.watchlist;

import asteria.repository.WatchlistRepository;
import java.util.function.Consumer;

public class WatchlistServiceImpl implements WatchlistService {
    private final asteria.repository.WatchlistRepository repo;

    public WatchlistServiceImpl(WatchlistRepository repo) {
        this.repo = repo;
    }

    @Override
    public void refreshWatchlist(int userId, java.util.function.Consumer<java.util.List<asteria.model.WatchlistItem>> onSuccess, java.util.function.Consumer<Throwable> onError) {

        javafx.concurrent.Task<java.util.List<asteria.model.WatchlistItem>> task =
                new javafx.concurrent.Task<>() {
                    @Override protected java.util.List<asteria.model.WatchlistItem> call() throws Exception {
                        return repo.loadWatchlistItemsFromDb(userId);
                    }
                };

        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onError.accept(task.getException()));

        new Thread(task, "watchlist-service").start();
    }

    @Override
    public void addToWatchlist(int userId, String symbol, String nickname,
                               Runnable onSuccess,
                               java.util.function.Consumer<Throwable> onError) {

        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override protected Void call() throws Exception {
                repo.addToWatchlist(userId, symbol, nickname);
                return null;
            }
        };

        task.setOnSucceeded(e -> javafx.application.Platform.runLater(onSuccess));
        task.setOnFailed(e -> onError.accept(task.getException()));

        new Thread(task, "watchlist-add").start();
    }

    @Override
    public void removeFromWatchlist(int userId, String symbol, Runnable onSuccess, Consumer<Throwable> onError) {

        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override protected Void call() throws Exception {
                repo.removeFromWatchlist(userId, symbol);
                return null;
            }
        };

        task.setOnSucceeded(e -> javafx.application.Platform.runLater(onSuccess));
        task.setOnFailed(e -> onError.accept(task.getException()));

        new Thread(task, "watchlist-remove").start();
    }

}
