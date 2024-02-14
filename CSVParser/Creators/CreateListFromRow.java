package CSVParser.Creators;

import java.util.List;

public class CreateListFromRow implements CreatorFromRow<List<String>> {
  @Override
  public List<String> create(List<String> row) {
    return row;
  }
}
