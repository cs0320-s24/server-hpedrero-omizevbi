package edu.brown.cs.student.main.CSVParser;

import edu.brown.cs.student.main.CSVParser.Creators.CreatorFromRow;
import edu.brown.cs.student.main.Exceptions.CSVParserException;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
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

  /**
   * Constructs a new CSVParser object with the specified parameters.
   *
   * @param reader  The Reader object for reading data from the CSV file.
   * @param creator The CreatorFromRow object for creating type T objects from CSV rows.
   * @param hasHeaders A boolean indicating whether the CSV data has headers.
   * @throws IllegalArgumentException If the reader or creator is null.
   * @throws IOException If an I/O error occurs while reading the CSV data.
   * @throws FactoryFailureException If an error occurs during the creation of type T objects.
   * @throws CSVParserException If an error occurs while parsing the CSV data.
   */
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
   * Parses the CSV data from the reader, creates objects of type T from each row, and populates the list of typeObjects.
   *
   * @throws IOException               If an I/O error occurs while reading the CSV data.
   * @throws FactoryFailureException   If the CSV data is formatted incorrectly.
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

  /**
   * Gets the list of parsed objects of type T.
   *
   * @return The list of parsed objects of type T.
   */
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

  /**
   * Gets the list of headers if the CSV data has headers.
   *
   * @return The list of headers, or null if the CSV data does not have headers.
   */
  public List<String> getHeaders() {
    return headers;
  }
}
