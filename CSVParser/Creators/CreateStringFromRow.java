package CSVParser.Creators;

import Exceptions.FactoryFailureException;
import java.util.List;

public class CreateStringFromRow implements CreatorFromRow<String> {
  @Override
  public String create(List<String> row) throws FactoryFailureException {
    try {
      return String.join("", row);
    } catch (Exception e) {
      throw new FactoryFailureException(
          "An unexpected error occurred while creating string from row", row);
    }
  }
}
