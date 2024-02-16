package edu.brown.cs.student.Tests.CSVTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import edu.brown.cs.student.main.CSVParser.Search;
import edu.brown.cs.student.main.Exceptions.CSVParserException;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/** Tests different StringReader and FileReader scenarios */
public class TestReaders {
  private CSVParser<List<String>> parser;
  private Search search;

  /**
   * Basic test using String reader on normally formatted CSV string. Verifies correct processing &
   * expected number of rows.
   */
  @Test
  public void testNormalReader() throws IOException, FactoryFailureException, CSVParserException {
    StringReader reader = new StringReader("header1,header2\nvalue1,value2\nvalue3,value4");
    CSVParser<List<String>> parser = new CSVParser<>(reader, new CreateListFromRow(), true);
    assertEquals(2, parser.getParsed().size());
  }

  /** Basic test on empty string case, verifies that it is empty. */
  @Test
  public void testEmptyReader() throws IOException, FactoryFailureException, CSVParserException {
    StringReader reader = new StringReader("");
    CSVParser<List<String>> parser = new CSVParser<>(reader, new CreateListFromRow(), false);
    assertTrue(parser.getParsed().isEmpty());
  }

  /** Test to check for exception with malformed data. */
  @Test
  public void testMalformedData() {
    StringReader reader = new StringReader("value1,value2\nvalue3");
    assertThrows(
        FactoryFailureException.class,
        () -> new CSVParser<>(reader, new CreateListFromRow(), true));
  }

  /** Test to check inconsistent rows in data w/o headers */
  @Test
  public void testInconsistentData()
      throws IOException, FactoryFailureException, CSVParserException {
    StringReader reader = new StringReader("value1,value2\nvalue3,value4,value5");
    CSVParser<List<String>> parser = new CSVParser<>(reader, new CreateListFromRow(), false);
    assertEquals(2, parser.getParsed().size());
  }

  /**
   * Test to check for expected result when searching one of the given CSV files. Checks every
   * column, specific column, and wrong column
   */
  @Test
  public void testCSVWithFileSingle()
      throws IOException, FactoryFailureException, CSVParserException {
    this.parser =
        new CSVParser<>(new FileReader("data/stars/stardata.csv"), new CreateListFromRow(), true);
    this.search = new Search(this.parser);
    List<List<String>> expected = new ArrayList<>();
    expected.add(
        Arrays.asList(new String[] {"6584", "Bena", "195.22164", "75.65553", "628.28496"}));
    assertEquals(this.search.searchEveryColumn("Bena"), expected);
    assertEquals(this.search.searchColumn(1, "Bena"), expected);
    assertTrue(this.search.searchColumn(0, "Bena").isEmpty());
  }

  /**
   * Test to check for expected result when searching one of the given CSV files, and there are
   * multiple results in the file.
   */
  @Test
  public void testCSVWithFileMultiple()
      throws IOException, FactoryFailureException, CSVParserException {
    this.parser =
        new CSVParser<>(
            new FileReader("data/census/postsecondary_education.csv"),
            new CreateListFromRow(),
            true);
    this.search = new Search(this.parser);
    List<List<String>> expected = new ArrayList<>();
    expected.add(
        Arrays.asList(
            new String[] {
              "Hispanic or Latino",
              "2020",
              "2020",
              "217156",
              "Brown University",
              "143",
              "brown-university",
              "0.046263345",
              "Men",
              "1"
            }));
    expected.add(
        Arrays.asList(
            new String[] {
              "Hispanic or Latino",
              "2020",
              "2020",
              "217156",
              "Brown University",
              "207",
              "brown-university",
              "0.066968619",
              "Women",
              "2"
            }));
    assertEquals(this.search.searchEveryColumn("Hispanic or Latino"), expected);
  }
}
