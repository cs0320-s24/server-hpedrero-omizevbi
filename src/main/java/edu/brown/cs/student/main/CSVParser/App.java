package edu.brown.cs.student.main.CSVParser;

import static java.lang.Integer.parseInt;

import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

/**
 * Basic implementation to search a csv that asks the user for information using a scanner and
 * performs the search on a csv file. Prints out the resulting rows that satisfy the user's search
 * query.
 */
public class App {
  public void runApp() {
    // try to open scanner for user input
    try (Scanner scanner = new Scanner(System.in)) {
      // only allow data directory to protect access to other directories
      System.out.println("file path (within data):");
      String filePath = "data/" + scanner.nextLine();

      FileReader fileReader;
      try {
        // attempt to open file
        fileReader = new FileReader(filePath);
      } catch (FileNotFoundException e) {
        System.err.println("error: file not found - " + e.getMessage());
        return;
      }

      // prompt for search term
      System.out.println("search term:");
      String searchTerm = scanner.nextLine();

      // allow search by header, index, or any column
      System.out.println(
          "type h to search by header, i to search by index, and anything else to search all columns");
      String searchType = scanner.nextLine();

      // initialize parser & search
      CSVParser<List<String>> parser;
      try {
        parser =
            new CSVParser<>(
                fileReader,
                new CreateListFromRow(),
                searchType.equals("h") || searchType.equals("i"));
      } catch (Exception e) {
        System.err.println("error running CSV Parser:" + e.getMessage());
        return;
      }
      Search search = new Search(parser);

      String column;
      List<List<String>> results;

      // define behavior depending on search type
      switch (searchType) {
        case ("h") -> {
          System.out.println("column header:");
          column = scanner.nextLine();
          if (!parser.getHeaders().contains(column)) {
            System.err.println("error - header not found: " + column);
            return;
          }
          results = search.searchColumn(parser.getHeaders().indexOf(column), searchTerm);
        }
        case ("i") -> {
          System.out.println("column index:");
          column = scanner.nextLine();
          try {
            results = search.searchColumn(parseInt(column), searchTerm);
          } catch (NumberFormatException e) {
            System.err.println("error: invalid index - " + e.getMessage());
            return;
          }
        }
        default -> results = search.searchEveryColumn(searchTerm);
      }

      // print result of search
      if (results.isEmpty()) {
        System.out.println("no results found.");
      } else {
        for (List<String> result : results) {
          System.out.println(result);
        }
      }
      // catch any uncaught exceptions
    } catch (Exception e) {
      System.err.println("error - " + e.getMessage());
    }
  }
}
