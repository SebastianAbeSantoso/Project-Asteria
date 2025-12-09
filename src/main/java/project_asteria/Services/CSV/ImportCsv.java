package project_asteria.Services.CSV;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public interface ImportCsv {
    public void importPriceCsv(String symbol, Path csvPath) throws SQLException, IOException;
}
