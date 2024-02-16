package edu.brown.cs.student.Tests.CSVTests;

import static org.junit.Assert.*;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import edu.brown.cs.student.main.Exceptions.CSVParserException;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/** Tests different scenarios the livecode regex & post-processing should handle */
public class TestRegex {

  private CSVParser<List<String>> parser;

  /** Sets up dummy CSVParser instance to test splitLine */
  @Before
  public void setup() throws IOException, FactoryFailureException, CSVParserException {
    this.parser = new CSVParser<>(new StringReader(""), new CreateListFromRow(), false);
  }

  /** Tests basic CSV */
  @Test
  public void testRegexWithSimpleCSV() {
    String line = "1,2,3";
    List<String> result = parser.splitLine(line);
    assertEquals(List.of("1", "2", "3"), result);
  }

  /** Tests for handling of commas inside quoted strings */
  @Test
  public void testRegexWithCommaInsideQuotes() {
    String line = "\"1, still 1\",\"2\",\"3\"";
    List<String> result = parser.splitLine(line);
    assertEquals(List.of("1, still 1", "2", "3"), result);
  }

  /** Tests for handling of mix of quoted/unquoted */
  @Test
  public void testRegexWithMixedQuotes() {
    String line = "\"1\",\"2, still 2\",3";
    List<String> result = parser.splitLine(line);
    assertEquals(List.of("1", "2, still 2", "3"), result);
  }

  /** Tests for handling of double quotes */
  @Test
  public void testRegexWithDoubleQuotes() {
    String line = "\"1\",\"\"\"2\"\" quoted\",\"3\"";
    List<String> result = parser.splitLine(line);
    assertEquals(List.of("1", "\"2\" quoted", "3"), result);
  }
}
