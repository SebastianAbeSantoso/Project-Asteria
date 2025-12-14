package asteria.services.dataimport.csv;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public interface CsvImporter {
    public void importPriceCsv(String symbol, Path csvPath) throws SQLException, IOException;
}
