package edu.brown.cs.student.Tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.Creators.CreateBooleanFromRow;
import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import edu.brown.cs.student.main.CSVParser.Creators.CreateStringFromRow;
import edu.brown.cs.student.main.CSVParser.Creators.CreateSumFromRow;
import edu.brown.cs.student.main.Exceptions.CSVParserException;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

/** Tests different CreatorFromRow implementations */
public class TestCreators {

  /** Test for the CreateListFromRow. Ensures list of strings is correctly created. */
  @Test
  public void testListFromRow() throws FactoryFailureException {
    CreateListFromRow creator = new CreateListFromRow();
    List<String> row = Arrays.asList("Hello", "World");
    assertEquals(row, creator.create(row));
  }

  /** Test for the CreateStringFromRow. Ensures string list is correctly concatenated. */
  @Test
  public void testStringFromRow() throws FactoryFailureException {
    CreateStringFromRow creator = new CreateStringFromRow();
    List<String> row = Arrays.asList("Hello", "World");
    assertEquals("HelloWorld", creator.create(row));
  }

  /** Test for CreateSumFromRow. Ensures list of numeric string values is summed properly. */
  @Test
  public void testSumFromRow() throws FactoryFailureException {
    CreateSumFromRow creator = new CreateSumFromRow();
    List<String> row = Arrays.asList("1", "2", "3");
    assertEquals(Integer.valueOf(6), creator.create(row));
  }

  /** Test for CreateSumFromRow. Tests case where a non-numeric value is in the csv. */
  @Test(expected = FactoryFailureException.class)
  public void testSumFromRowInvalid() throws FactoryFailureException {
    CreateSumFromRow creator = new CreateSumFromRow();
    List<String> row = Arrays.asList("1", "a", "3");
    creator.create(row);
  }

  /**
   * Test for CreateBooleanFromRow. Makes sure that list of strings is properly formatted to a
   * boolean.
   */
  @Test
  public void testBooleanFromRow() {
    CreateBooleanFromRow creator = new CreateBooleanFromRow();
    List<String> row1 = Arrays.asList("false", "dansd", "true");
    List<String> row2 = Arrays.asList("false", "daskndpkna", "askdno");
    assertTrue(creator.create(row1));
    assertFalse(creator.create(row2));
  }

  /**
   * Test to ensure one of the abitrary CreatorFromRow implementations also works when used with
   * CSVParser. Uses CreateSumFromRow to sum the rows of the CSV and checks whether sums are
   * correct.
   */
  @Test
  public void testRowSum() throws FactoryFailureException, CSVParserException, IOException {
    String mockCSV = "1,2,3\n4,5,6\n7,8,9";
    StringReader reader = new StringReader(mockCSV);
    CSVParser<Integer> parser = new CSVParser<>(reader, new CreateSumFromRow(), false);
    List<Integer> expectedSums = Arrays.asList(6, 15, 24);
    assertEquals(expectedSums, parser.getParsed());
  }
}
