import com.example.dsl.DSLParser;
import com.example.dsl.SQLGenerator;
import com.example.antlr.SQLGeneratorParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SQLGeneratorTest {

    @Test
    public void testBasicTableWithColumns() throws Exception {
        String dslInput = """
            table users:
                id (entier, clé primaire, auto)
                name (texte, requis)
            """;

        ParseTree tree = DSLParser.parse(dslInput);
        SQLGenerator generator = new SQLGenerator();
        String sql = generator.generate(tree);

        String expectedSQL = """
            CREATE TABLE users (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL
            );
            """;

        assertEquals(expectedSQL.trim(), sql.trim());
    }

    @Test
    public void testTableWithForeignKey() throws Exception {
        String dslInput = """
            table orders:
                orderId (entier, clé primaire, auto)
                userId (entier, requis)
                référence: userId -> users.id
            table users:
                id (entier, clé primaire, auto)
            """;

        ParseTree tree = DSLParser.parse(dslInput);
        SQLGenerator generator = new SQLGenerator();
        String sql = generator.generate(tree);

        String expectedSQL = """
            CREATE TABLE orders (
                orderId INT PRIMARY KEY AUTO_INCREMENT,
                userId INT NOT NULL,
                FOREIGN KEY (userId) REFERENCES users(id)
            );

            CREATE TABLE users (
                id INT PRIMARY KEY AUTO_INCREMENT
            );
            """;

        assertEquals(expectedSQL.trim(), sql.trim());
    }

    @Test
    public void testTableWithDefaultConstraint() throws Exception {
        String dslInput = """
            table users:
                id (entier, clé primaire, auto)
                createdAt (date, par défaut: "2023-01-01")
            """;

        ParseTree tree = DSLParser.parse(dslInput);
        SQLGenerator generator = new SQLGenerator();
        String sql = generator.generate(tree);

        String expectedSQL = """
            CREATE TABLE users (
                id INT PRIMARY KEY AUTO_INCREMENT,
                createdAt DATE DEFAULT '2023-01-01'
            );
            """;

        assertEquals(expectedSQL.trim(), sql.trim());
    }

    @Test
    public void testEmptyInput() {
        String dslInput = "";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ParseTree tree = DSLParser.parse(dslInput);
            SQLGenerator generator = new SQLGenerator();
            generator.generate(tree);
        });

        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    public void testUnknownDataType() {
        // Mock the ParseTree for the table definition
        SQLGeneratorParser.ProgramContext programContext = mock(SQLGeneratorParser.ProgramContext.class);
        SQLGeneratorParser.TableDefContext tableDefContext = mock(SQLGeneratorParser.TableDefContext.class);
        SQLGeneratorParser.ColumnDefContext columnDefContext = mock(SQLGeneratorParser.ColumnDefContext.class);
        SQLGeneratorParser.TypeContext typeContext = mock(SQLGeneratorParser.TypeContext.class);
        SQLGeneratorParser.TableNameContext tableNameContext = mock(SQLGeneratorParser.TableNameContext.class);
        SQLGeneratorParser.ColumnNameContext columnNameContext = mock(SQLGeneratorParser.ColumnNameContext.class);

        // Mock the parse tree structure
        when(programContext.getChildCount()).thenReturn(1);
        when(programContext.getChild(0)).thenReturn(tableDefContext);

        when(tableDefContext.tableName()).thenReturn(tableNameContext);
        when(tableNameContext.getText()).thenReturn("users");

        when(tableDefContext.getChildCount()).thenReturn(1);
        when(tableDefContext.getChild(0)).thenReturn(columnDefContext);
        when(columnDefContext.columnName()).thenReturn(columnNameContext);
        when(columnNameContext.getText()).thenReturn("id");

        when(columnDefContext.type()).thenReturn(typeContext);
        when(typeContext.getText()).thenReturn("unknownType");

        // Initialize SQLGenerator
        SQLGenerator generator = new SQLGenerator();

        // Verify that the SQLGenerator throws an exception for unknown data type
        Exception exception = assertThrows(RuntimeException.class, () -> {
            generator.generate(programContext);
        });
        System.out.println("here's the exception message: ");
        System.out.println(exception.getMessage());

        assertTrue(exception.getMessage().contains("Unknown data type: unknownType"));
    }
}
