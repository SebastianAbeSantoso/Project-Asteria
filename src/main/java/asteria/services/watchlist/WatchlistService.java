package asteria.services.watchlist;

import java.util.function.Consumer;

public interface WatchlistService {
    void refreshWatchlist(int userId, java.util.function.Consumer<java.util.List<asteria.model.WatchlistItem>> onSuccess, java.util.function.Consumer<Throwable> onError);
    void addToWatchlist(int userId, String symbol, String nickname, Runnable onSuccess, java.util.function.Consumer<Throwable> onError);
    void removeFromWatchlist(int userId, String symbol, Runnable onSuccess, Consumer<Throwable> onError);
}
