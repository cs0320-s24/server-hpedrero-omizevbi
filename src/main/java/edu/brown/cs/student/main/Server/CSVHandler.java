package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import edu.brown.cs.student.main.CSVParser.ProxyCache;
import edu.brown.cs.student.main.CSVParser.Search;
import java.io.FileReader;
import java.util.concurrent.TimeUnit;

import spark.Request;
import spark.Response;
import spark.Route;

public class CSVHandler implements Route {

  private CSVParser parser;
  private Search search;
  private ProxyCache parserProxy;
  public Object handle(Request request, Response response) throws Exception {
    String action = request.queryParams("action");
    switch (action) {
      case "loadcsv":
        return this.loadcsv(request, response);
      case "viewcsv":
        return this.parserProxy.loadData(request.queryParams("path"));
      case "searchcsv":
        return this.searchcsv(request, response);
      default:
        return "Invalid action";
    }
  }

  public Object loadcsv(Request request, Response response) throws Exception {
    String path = request.queryParams("path");
    try {
      this.parserProxy = new ProxyCache<>(
              new FileReader(path),
              new CreateListFromRow(),
              true,
              100,
              30,
              TimeUnit.SECONDS);
      return "CSV loaded successfully";
    } catch (Exception e) {
      return "Failed to load CSV";
    }
  }

  public Object searchcsv(Request request, Response response) throws Exception {
    String query = request.queryParams("query");
    String path = request.queryParams("path");
    return this.parserProxy.search(path, query);
  }

}
