> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details

- **Project Name:** Server
- **Project Description:** A Java library for parsing CSV files and converting them into objects.
- **Team Members and Contributions:**
    - Osamuyimen Izevbigie (omizevbi): Implemented cache parsing functionality, wrote javadocs, wrote readme 
    - Harrison Pedrero  (hpedrero): Designed and implemented object creation from CSV rows, fixed caching implementation, implemented search functionality, implemented handlers for both CSV and Census    
- **Total Estimated Time:** 20 hours
- **Repository:** [Link to Repository](https://github.com/cs0320-s24/server-hpedrero-omizevbi.git)

# Design Choices

- **Relationships Between Classes/Interfaces:**
    - The `CSVParser` class handles the parsing of CSV files and creation of objects using a `CreatorFromRow` implementation.
    - The `CreatorFromRow` interface defines a method for creating objects from CSV rows, allowing for customization.
- **Specific Data Structures:**
    - Lists are used to store parsed objects and CSV headers.
    - BufferedReaders are used for efficient reading of CSV files.
- **Runtime/Space Optimizations:**
    - Loading of data from CSV files to minimize memory usage.
    - Caching of parsed data for improved performance on frequent accesses.

# Errors/Bugs

- **Bug 1: NullPointer Exception**
    - **Reproduction Steps:**
        1. Load a CSV file with null data.
        2. Attempt to parse the CSV file.
    - **Explanation:** The parser does not handle null data gracefully, leading to a NullPointerException.
- **Bug 2: Malformed CSV Data**
    - **Reproduction Steps:**
        1. Load a CSV file with inconsistent column counts.
        2. Attempt to parse the CSV file.
    - **Explanation:** The parser throws a FactoryFailureException when encountering CSV data with incorrect column counts.

# Checkstyle Errors

- No checkstyle errors were encountered in the project.

# Tests

- **Testing Suites:**
    - Unit tests for the `CSVParser` class to ensure correct parsing behavior.
    - Integration tests to verify object creation from CSV rows.
    - Edge cases testing for handling of empty CSV files and malformed data.

# How to

- **Run Tests:**
    - Execute `mvn test` in the project root directory to run all tests.
- **Build and Run Program:**
    - Use `mvn package` to build the project.
    - in your terminal then `./run` (using Git Bash for Windows users).
