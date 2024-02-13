package edu.brown.cs.student.main.CSVParser.Creators;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

/**
 * Implementation of the CreatorFromRow interface that creates a sum of integers from a list of
 * integers.
 */
public class CreateSumFromRow implements CreatorFromRow<Integer> {
  @Override
  public Integer create(List<String> row) throws FactoryFailureException {
    try {
      return row.stream().mapToInt(Integer::parseInt).sum();
    } catch (NumberFormatException e) {
      throw new FactoryFailureException("Invalid integer format", row);
    }
  }
}
