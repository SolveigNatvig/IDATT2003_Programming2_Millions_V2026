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
        return Files.readAllLines(Paths.get(filename)).stream()
            .map(String::trim)
            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
            .map(line -> line.split(","))
            .filter(parts -> parts.length == 3)
            .map(parts -> new Stock(parts[0], parts[1], new BigDecimal(parts[2])))
            .toList();
    }

    // lagring
    public void saveStocksToFile(String filename, List<Stock> stocks) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# Ticker,Name,Price");
        
        lines.addAll(stocks.stream()
            .map(stock -> String.format("%s,%s,%s", 
                stock.getSymbol(), 
                stock.getCompany(), 
                stock.getSalesPrice().toString()))
            .toList());
        
        Files.write(Paths.get(filename), lines);
    }
}