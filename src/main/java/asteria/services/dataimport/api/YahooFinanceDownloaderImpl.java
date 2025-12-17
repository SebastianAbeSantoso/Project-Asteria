package asteria.services.dataimport.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class YahooFinanceDownloaderImpl implements YahooFinanceDownloader {
    private final Path exePath;

    public YahooFinanceDownloaderImpl(Path exePath) {
        this.exePath = exePath;
    }

    public Path download(String symbol, Path outCsv) throws IOException, InterruptedException {

        Files.createDirectories(outCsv.getParent());

        Files.deleteIfExists(outCsv);

        ProcessBuilder pb = new ProcessBuilder(
                exePath.toAbsolutePath().toString(),
                symbol,
                outCsv.toAbsolutePath().toString()
        );

        pb.redirectErrorStream(true);
        Process p = pb.start();

        String log = new String(
                p.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        int exitCode = p.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Yahoo EXE failed:\n" + log);
        }

        if (!Files.exists(outCsv) || Files.size(outCsv) < 100) {
            throw new RuntimeException("CSV not created or empty.");
        }

        System.out.println("Yahoo EXE log:\n" + log);
        return outCsv;

    }
}
