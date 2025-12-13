package project_asteria.Services.CSV;

import project_asteria.Model.PriceCandle;
import project_asteria.Repository.PriceHistoryRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvImporter implements ImportCsv {
    private final PriceHistoryRepository priceRepo;

    public CsvImporter(PriceHistoryRepository priceRepo) {
        this.priceRepo = priceRepo;
    }

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void importPriceCsv (String symbol, Path csvPath) throws SQLException, IOException {
        List<PriceCandle> candles = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)){
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                if (lineCount < 3) {
                    lineCount++;
                    continue;
                }
                String[] parts = line.split(",");

                LocalDate date = LocalDate.parse(parts[0], dateFormatter);
                double open = Double.parseDouble(parts[5]);
                double high = Double.parseDouble(parts[3]);
                double low = Double.parseDouble(parts[4]);
                double close = Double.parseDouble(parts[2]);
                double volume = parts.length > 6 ? Double.parseDouble(parts[6]) : 0.0;

                candles.add(new PriceCandle(date, open, high, low, close, volume));
            }
        } catch (NumberFormatException  | IndexOutOfBoundsException e) {
            System.err.println("Skipping invalid row: ");
        }

        try {
            priceRepo.saveCandles(symbol, candles);
            System.out.println("Imported " + candles.size() + " rows for symbol " + symbol);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import candles to DB", e);
        }
    }
}
