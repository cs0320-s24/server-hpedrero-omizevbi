package edu.brown.cs.student.main.Server;

import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class CensusHandler implements Route {

  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseMap = new HashMap<>();

    try {
      String stateCode = getStateCode(state);
      String countyCode = getCountyCode(stateCode, county);
      String data = getCensusData(stateCode, countyCode);
      responseMap.put("timestamp", System.currentTimeMillis());
      responseMap.put("state", state);
      responseMap.put("county", county);
      responseMap.put("censusData", data);
      responseMap.put("result", "success");
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "error");
      responseMap.put("message", e.getMessage());
    }
    return responseMap;
  }

  private String getStateCode(String stateName) throws URISyntaxException, IOException, InterruptedException {
    String url = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

    HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> jsonAdapter = moshi.adapter(listType);

    List<List<String>> censusResponse = jsonAdapter.fromJson(response.body());

    String stateCode = "";
    if (censusResponse != null) {
      for (List<String> item : censusResponse) {
        if (item.get(0).equalsIgnoreCase(stateName)) {
          stateCode = item.get(1);
          break;
        }
      }
    }

    if (stateCode.isEmpty()) {
      throw new IllegalArgumentException("no state code found for " + stateName);
    }

    return stateCode;

  }

  private String getCountyCode(String stateCode, String countyName) throws URISyntaxException, IOException, InterruptedException {
    String url = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
        + stateCode;
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

    HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

    Moshi moshi = new Moshi.Builder().build();
    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> jsonAdapter = moshi.adapter(listType);

    List<List<String>> censusResponse = jsonAdapter.fromJson(response.body());

    String countyCode = "";
    if (censusResponse != null) {
      for (List<String> item : censusResponse) {
        if (item.get(0).startsWith(countyName)) {
          countyCode = item.get(2);
          break;
        }
      }
    }

    if (countyCode.isEmpty()) {
      throw new IllegalArgumentException("no county code found for " + countyName);
    }

    return countyCode;
  }

  private String getCensusData(String stateCode, String countyCode) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:"
            + countyCode + "&in=state:" + stateCode)).GET().build();

    HttpResponse<String> response = HttpClient.newBuilder().build()
            .send(request, HttpResponse.BodyHandlers.ofString());

    return response.body();
  }
}
