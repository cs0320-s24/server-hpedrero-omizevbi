package edu.brown.cs.student.main.CSVParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic Search implementation - uses the CSVParser class with the CreateListFromRow implementation
 * of the CreatorFomRow interface to perform a search on a CSV file.
 */
public class Search {

  private final List<List<String>> rows;

  public Search(CSVParser<List<String>> parser) {
    this.rows = parser.getParsed();
  }

  /**
   * Loops through every row and checks if specified column contains the search term
   *
   * @param searchTerm the term to be searched
   * @param index the column to be searched
   * @return the resulting list of rows containing the search term
   */
  public List<List<String>> searchColumn(int index, String searchTerm) {
    List<List<String>> result = new ArrayList<>();
    for (List<String> row : rows) {
      if (row.get(index).equals(searchTerm)) {
        result.add(row);
      }
    }
    return result;
  }

  /**
   * Loops through every row and checks if it contains the search term
   *
   * @param searchTerm the term to be searched
   * @return the resulting list of rows containing the search term
   */
  public List<List<String>> searchEveryColumn(String searchTerm) {
    List<List<String>> result = new ArrayList<>();
    for (List<String> row : rows) {
      if (row.contains(searchTerm)) {
        result.add(row);
      }
    }
    return result;
  }
}
