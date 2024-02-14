package CSVParser;

import CSVParser.Creators.CreatorFromRow;
import Exceptions.CSVParserException;
import Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Basic CSVParser implementation, that parses a CSV and creates a list of type objects created
 * through the inputted implementation of the CreatorFromRow interface.
 */
public class CSVParser<T> {

  private final BufferedReader reader;
  private final CreatorFromRow<T> creator;
  private final boolean hasHeaders;
  private List<String> headers;
  private List<T> typeObjects;

  // (COPIED DIRECTLY FROM LIVECODE REPOSITORY)
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /** Constructor - initializes instance variables and calls parse */
  public CSVParser(Reader reader, CreatorFromRow<T> creator, boolean hasHeaders)
      throws IOException, FactoryFailureException, CSVParserException {

    // check for null inputs
    if (reader == null || creator == null) {
      throw new IllegalArgumentException("Reader / CreatorFromRow can't be null");
    }

    this.reader = new BufferedReader(reader);
    this.creator = creator;
    this.hasHeaders = hasHeaders;
    try {
      this.parse();
      // if parse fails throw CSVParserException
    } catch (IOException e) {
      throw new CSVParserException("Error reading CSV data", e);
    }
  }

  /**
   * parses the csv - creates headers, reads & splits every line, initializes list of typeObjects
   */
  private void parse() throws IOException, FactoryFailureException {
    List<T> typeObjects = new ArrayList<>();
    String line;

    // skip header if exists
    if (hasHeaders) this.headers = splitLine(this.reader.readLine());

    // loop until end of reader
    while ((line = reader.readLine()) != null) {
      // split using regex and postprocess
      List<String> valueList = splitLine(line);

      // throw exception if data is formatted incorrectly
      if (this.hasHeaders && valueList.size() != this.headers.size()) {
        throw new FactoryFailureException("malformed csv: incorrect no. columns", valueList);
      }

      // convert to object of type T
      T obj = creator.create(valueList);
      // add object to list
      typeObjects.add(obj);
    }
    this.typeObjects = typeObjects;
  }

  public List<T> getParsed() {
    return this.typeObjects;
  }

  /**
   * Splits a line with regex and applies postprocessing
   *
   * @param line the line
   * @return the list of split & post-processed strings
   */
  public List<String> splitLine(String line) {
    // use regex to split line
    String[] strings = regexSplitCSVRow.split(line);
    String[] postProcessed = new String[strings.length];

    // use post-processing to process split strings
    for (int i = 0; i < strings.length; i++) {
      postProcessed[i] = postprocess(strings[i]);
    }

    // return split & processed list
    return Arrays.asList(postProcessed);
  }

  /**
   * (COPIED DIRECTLY FROM LIVECODE REPOSITORY) Eliminate a single instance of leading or trailing
   * double-quote, and replace pairs of double quotes with singles.
   *
   * @param arg the string to process
   * @return the post-processed string
   */
  public static String postprocess(String arg) {
    return arg
        // Remove extra spaces at beginning and end of the line
        .trim()
        // Remove a beginning quote, if present
        .replaceAll("^\"", "")
        // Remove an ending quote, if present
        .replaceAll("\"$", "")
        // Replace double-double-quotes with double-quotes
        .replaceAll("\"\"", "\"");
  }

  public List<String> getHeaders() {
    return headers;
  }
}
