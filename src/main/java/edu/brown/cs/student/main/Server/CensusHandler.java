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

  /**
   * Constructs a new CensusHandler with the specified ProxyCache for caching census data.
   *
   * @param cache The ProxyCache instance to be used for caching.
   */
  public CensusHandler(ProxyCache cache) {
    this.cache = cache;
  }

  /**
   * Handles the HTTP request by retrieving census data based on the specified state and county (optional) parameters.
   *
   * @param request  The HTTP request object containing query parameters.
   * @param response The HTTP response object to be populated with the result.
   * @return A map containing the timestamp, state, county (if specified), census data, and result of the operation.
   */
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

  /**
   * Retrieves the state code for the given state name by querying the Census API.
   *
   * @param stateName The name of the state for which to retrieve the state code.
   * @return The state code corresponding to the given state name.
   * @throws URISyntaxException    If the URI syntax is invalid.
   * @throws IOException           If an I/O error occurs while sending or receiving the HTTP request.
   * @throws InterruptedException If the thread is interrupted while waiting for the request to complete.
   * @throws IllegalArgumentException If no state code is found for the given state name.
   */
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

  /**
   * Retrieves the county code for the given county name and state code by querying the Census API.
   *
   * @param stateCode  The state code for which to retrieve the county code.
   * @param countyName The name of the county for which to retrieve the county code.
   * @return The county code corresponding to the given county name and state code.
   * @throws URISyntaxException    If the URI syntax is invalid.
   * @throws IOException           If an I/O error occurs while sending or receiving the HTTP request.
   * @throws InterruptedException If the thread is interrupted while waiting for the request to complete.
   * @throws IllegalArgumentException If no county code is found for the given county name and state code.
   */
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

  /**
   * Retrieves census data for the specified state and county (optional) from the Census API.
   *
   * @param stateCode   The code representing the state for which census data is to be retrieved.
   * @param countyCode  The code representing the county for which census data is to be retrieved, or "*" for statewide data.
   * @param isStateWide A flag indicating whether the query is for statewide data.
   * @return The census data retrieved from the Census API.
   * @throws URISyntaxException    If the URI endpoint is invalid.
   * @throws IOException           If an I/O error occurs while sending or receiving the HTTP request.
   * @throws InterruptedException If the HTTP request is interrupted while waiting for the response.
   */
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
