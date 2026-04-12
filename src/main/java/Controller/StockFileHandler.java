package Controller;
import java.nio.file.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import Model.Stock;

public class StockFileHandler {

    // lesing
    public List<Stock> loadStocksFromFile(String filename) throws IOException {
        List<Stock> stocks = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String symbol = parts[0];
                String name = parts[1];
                BigDecimal price = new BigDecimal(parts[2]);
                
                stocks.add(new Stock(symbol, name, price));
            }
        }
        return stocks; // returnerer listen 
    }

    // lagring
    public void saveStocksToFile(String filename, List<Stock> stocks) throws IOException {
        List<String> lines = new ArrayList<>();
        
        // header 
        lines.add("# Ticker,Name,Price");

        for (Stock stock : stocks) {
            // formaterer hver aksje som "SYMBOL,NAME,PRICE"
            String line = String.format("%s,%s,%s", 
                stock.getSymbol(), 
                stock.getCompany(), 
                stock.getSalesPrice().toString());
            lines.add(line);
        }

        Files.write(Paths.get(filename), lines);
    }
}