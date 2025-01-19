

import com.example.dsl.DSLParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DSLParserTest {

    @Test
    public void testValidDSL() {
        String validDSL = """
            table users:
                id (entier, clé primaire, auto)
                name (texte, requis)
                createdAt (date, par défaut: aujourd'hui)
            """;
        assertDoesNotThrow(() -> {
            ParseTree tree = DSLParser.parse(validDSL);
            assertNotNull(tree, "ParseTree should not be null for valid input.");
        });
    }

    @Test
    public void testInvalidDSL_MissingTableName() {
        String invalidDSL = """
            table :
                id (entier, clé primaire)
            """;
        Exception exception = assertThrows(RuntimeException.class, () -> {
            DSLParser.parse(invalidDSL);
        });
        assertTrue(exception.getMessage().contains("Syntax error"), "Expected syntax error for missing table name.");
    }

    @Test
    public void testInvalidDSL_UnexpectedToken() {
        String invalidDSL = """
            table users:
                id (invalidtype)
            """;
        Exception exception = assertThrows(RuntimeException.class, () -> {
            DSLParser.parse(invalidDSL);
        });
        assertTrue(exception.getMessage().contains("invalidtype"), "Expected syntax error for invalid data type.");
    }

    @Test
    public void testEmptyDSL() {
        String emptyDSL = "";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            DSLParser.parse(emptyDSL);
        });
        assertTrue(exception.getMessage().contains("line 1"), "Expected syntax error at the start of the DSL.");
    }
}
