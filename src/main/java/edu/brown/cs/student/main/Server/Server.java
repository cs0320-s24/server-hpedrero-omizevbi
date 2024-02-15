package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import spark.Spark;

public class Server {
  public static void main(String[] args) {
    int port = 6969;
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    /* example of commands i tested that worked:
     * http://localhost:6969/csv?action=loadcsv&path=data/stars/stardata.csv
     * http://localhost:6969/csv?action=viewcsv
     * http://localhost:6969/csv?action=searchcsv&query=Bena
     * http://localhost:6969/csv?action=searchcsv&query=1604
     *
     * http://localhost:6969/census?state=Rhode%20Island&county=Bristol%20County
     */
    Spark.get("/csv", new CSVHandler());
    Spark.get("/census", new CensusHandler());

    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
