# SQL Generator Project

## Overview
This project is a desktop application that allows users to generate SQL scripts based on a custom DSL (Domain-Specific Language) description of database table structures. The application ensures the input is syntactically and semantically valid before generating the SQL scripts. It features a GUI interface for user interaction.

---

## Features

- **DSL Parsing**: Parse textual descriptions of database table structures.
- **Validation**: Validate the DSL for:
  - Syntax errors.
  - Semantic correctness (e.g., constraints and foreign keys).
  - Type-specific rules (e.g., only `date` types can have default values).
- **SQL Generation**: Generate SQL `CREATE TABLE` statements from the validated DSL input.
- **File Support**: Ability to load DSL input from `.txt` files.
- **Error Reporting**: Display error messages for both syntaxically-invalid and semantically-invalid DSL inputs.

---

## DSL Syntax
The DSL uses the following syntax to describe database tables:

### Table Definition
```
table <table_name>:
    <column_name> (<type>, <constraint>, ...)
    [<foreign_key_definition>]
```

### Supported Types
- `entier`: Maps to `INT` in SQL.
- `texte`: Maps to `VARCHAR(255)` in SQL.
- `date`: Maps to `DATE` in SQL.

### Supported Constraints
- `clé primaire`: Marks the column as a primary key.
- `auto`: Marks the column as auto-increment.
- `requis`: Marks the column as `NOT NULL`.
- `unique`: Adds a `UNIQUE` constraint.
- `par défaut`: Specifies a default value (only for `date` type) (supports two predefined default DSL values, 'aujourd'hui' which stands for the current day's date and "maintenant" which in addition to the current day's date appends the current time; if you'd like to use another date as the default value, it must follow the format "yyyy-MM-dd").

### Example DSL & Output
```plaintext
table users:
    id (entier, clé primaire, auto)
    name (texte, requis)
    createdAt (date, par défaut: aujourd'hui)

table orders:
    orderId (entier, clé primaire, auto)
    userId (entier, requis)
    référence: userId -> users.id
```

Given the above DSL, the generated SQL would be:
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    createdAt DATE DEFAULT '2025-01-19'
);

CREATE TABLE orders (
    orderId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id)
);
```

---

## Technologies Used

- **Java**: Core programming language.
- **JavaFX**: GUI framework for the desktop application.
- **ANTLR**: Grammar-based parsing of the DSL.
- **Maven**: Build and dependency management.

---

## How to Run

1. **Prerequisites**:
   - JDK 17 or later.
   - Maven installed.

2. **Build the Project**:
   ```bash
   mvn clean package
   ```

3. **Run the Application**:
   - Using Maven:
     ```bash
     mvn javafx:run
     ```
   - Using the executable JAR:
     ```bash
     java -jar target/sqlgen-1.0-SNAPSHOT.jar
     ```


---

## License
This project is licensed under the [MIT License](LICENSE).
