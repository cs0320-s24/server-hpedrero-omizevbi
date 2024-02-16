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

  public Object searchcsv(Request request, Response response) throws Exception {
    String query = request.queryParams("query");
    return this.search.searchEveryColumn(query);
  }

}
