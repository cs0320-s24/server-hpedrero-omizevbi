package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.CSVParser.ProxyCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import spark.Spark;

public class Server {
  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    int port = 6969;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    ProxyCache cache = new ProxyCache(100, 30, TimeUnit.MINUTES);
    Spark.get("/csv", new CSVHandler());
    Spark.get("/census", new CensusHandler(cache));

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
