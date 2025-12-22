package asteria.services.watchlist;

public interface WatchlistService {
    void refreshWatchlist(int userId, java.util.function.Consumer<java.util.List<asteria.model.WatchlistItem>> onSuccess, java.util.function.Consumer<Throwable> onError);
}
