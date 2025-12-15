package asteria.services.dataimport.api;

import java.io.IOException;
import java.nio.file.Path;

public interface YahooFinanceDownloader {
    Path download(String symbol, Path outCsv) throws IOException, InterruptedException;
}
