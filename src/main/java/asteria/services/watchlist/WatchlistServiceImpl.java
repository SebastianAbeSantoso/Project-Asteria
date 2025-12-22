package asteria.services.watchlist;

public class WatchlistServiceImpl implements WatchlistService {
    private final asteria.repository.WatchlistRepository repo;

    public WatchlistServiceImpl(asteria.repository.WatchlistRepository repo) {
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
}
