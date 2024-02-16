package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import edu.brown.cs.student.main.CSVParser.Search;
import java.io.FileReader;
import spark.Request;
import spark.Response;
import spark.Route;

public class CSVHandler implements Route {

  private CSVParser parser;
  private Search search;

  /**
   * Handles the incoming request and performs the corresponding action based on the request parameters.
   *
   * @param request  The request object containing the incoming HTTP request.
   * @param response The response object for sending HTTP responses.
   * @return An object representing the result of the requested action. Returns "Invalid action" if the requested action is not recognized.
   * @throws Exception If an error occurs during request handling.
   */
  public Object handle(Request request, Response response) throws Exception {
    String action = request.queryParams("action");
    switch (action) {
      case "loadcsv":
        return this.loadcsv(request, response);
      case "viewcsv":
        return this.parser.getParsed();
      case "searchcsv":
        return this.searchcsv(request, response);
      default:
        return "Invalid action";
    }
  }

  /**
   * Loads a CSV file from the specified path and initializes the parser proxy with the data.
   *
   * @param request  The request object containing the HTTP request parameters.
   * @param response The response object for sending HTTP responses.
   * @return A string indicating the success or failure of loading the CSV file.
   *         Returns "CSV loaded successfully" if the CSV file is loaded successfully and the parser proxy is initialized.
   *         Returns "Failed to load CSV" if an error occurs during loading the CSV file or initializing the parser proxy.
   * @throws Exception If an error occurs during CSV file loading or parser proxy initialization.
   */
  public Object loadcsv(Request request, Response response) throws Exception {
    String path = request.queryParams("path");
    try {
      this.parser =
          new CSVParser<>(
              new FileReader(path),
              new CreateListFromRow(),
              true);
      this.search = new Search(this.parser);
      return "CSV loaded successfully";
    } catch (Exception e) {
      return "Failed to load CSV";
    }
  }

  /**
   * Searches for a query within the data loaded from a CSV file.
   *
   * @param request  The request object containing the HTTP request parameters.
   * @param response The response object for sending HTTP responses.
   * @return An object representing the result of the search operation.
   * @throws Exception If an error occurs during the search operation.
   */
  public Object searchcsv(Request request, Response response) throws Exception {
    String query = request.queryParams("query");
    return this.search.searchEveryColumn(query);
  }

}