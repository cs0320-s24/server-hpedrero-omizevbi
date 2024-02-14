package edu.brown.cs.student.Tests;

import static org.junit.Assert.assertThrows;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.Creators.CreateListFromRow;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.FileReader;
import org.junit.Test;

public class TestExceptions {
  /** Test for IllegalArgumentException when passing null arguments into CSVParser. */
  @Test
  public void testIllegalArguments() {
    assertThrows(IllegalArgumentException.class, () -> new CSVParser<>(null, null, false));
  }

  /** Test for FactoryFailureException when passing a corrupted file into the parser. */
  @Test
  public void testFactoryFailureException() {
    assertThrows(
        FactoryFailureException.class,
        () ->
            new CSVParser<>(
                new FileReader("data/corrupted/corrupted.csv"), new CreateListFromRow(), true));
  }
}
