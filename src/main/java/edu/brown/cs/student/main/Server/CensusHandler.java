package edu.brown.cs.student.main.Server;

import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSVParser.ProxyCache;
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
  private ProxyCache cache;

  public CensusHandler(ProxyCache cache) {
    this.cache = cache;
  }

  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county"); // null for query of entire state

    Map<String, Object> responseMap = new HashMap<>();

    try {
      String stateCode = getStateCode(state);
      String countyCode = county != null && !county.isEmpty() ? getCountyCode(stateCode, county) : "*";
      String cacheKey = "state:" + stateCode + "|county:" + countyCode;

      String data;
      if (cache != null && cache.getCachedData(cacheKey) != null) {
        data = cache.getCachedData(cacheKey);
      } else {
        data = getCensusData(stateCode, countyCode.equals("*") ? "*" : countyCode, countyCode.equals("*"));
        if (cache != null) {
          cache.putData(cacheKey, data);
        }
      }

      responseMap.put("timestamp", System.currentTimeMillis());
      responseMap.put("state", state);
      if (county != null && !county.isEmpty()) {
        responseMap.put("county", county);
      }
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

  private String getCensusData(String stateCode, String countyCode, boolean isStateWide) throws URISyntaxException, IOException, InterruptedException {
    String endpoint = isStateWide
        ? "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=state:" + stateCode
        : "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:" + countyCode
            + "&in=state:" + stateCode;

    HttpRequest request = HttpRequest.newBuilder().uri(new URI(endpoint)).GET().build();
    HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());

    return response.body();
  }
}
