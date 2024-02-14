package edu.brown.cs.student.main.CSVParser.Creators;

import java.util.List;

/**
 * Implementation of the CreatorFromRow interface that creates a Boolean based on whether the list
 * contains "true"
 */
public class CreateBooleanFromRow implements CreatorFromRow<Boolean> {
  @Override
  public Boolean create(List<String> row) {
    return row.contains("true");
  }
}
