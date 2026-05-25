package Controller;

import Model.Stock;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Handles loading and saving Stock data from/to CSV files.
 * The CSV format is: ticker,company_name,price
 * Lines starting with '#' are treated as comments and ignored.
 */
public class StockFileHandler {

  /**
   * Loads stocks from a CSV file.
   * Expected format: ticker,company,price
   * Empty lines and lines starting with '#' are ignored.
   *
   * @param filename the path to the CSV file
   * @return a list of Stock objects loaded from the file
   * @throws IOException if the file cannot be read
   */
  public List<Stock> loadStocksFromFile(String filename) throws IOException {
    return Files.readAllLines(Paths.get(filename)).stream()
        .map(String::trim)
        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
        .map(line -> line.split(","))
        .filter(parts -> parts.length == 3)
        .map(parts -> new Stock(parts[0], parts[1], new BigDecimal(parts[2])))
        .toList();
  }

  /**
   * Saves stocks to a CSV file. 
   * The file includes a header comment line followed by one stock per line.
   *
   * @param filename the path to the CSV file to write
   * @param stocks the list of stocks to save
   * @throws IOException if the file cannot be written
   */
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