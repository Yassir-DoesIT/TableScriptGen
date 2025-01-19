import com.example.dsl.DSLParser;
import com.example.dsl.Validator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidatorTest {

    @Test
    public void testValidDSL() {
        String validDSL = """
            table users:
                id (entier, clé primaire, auto)
                name (texte, requis)
                createdAt (date, par défaut: aujourd'hui)
            table orders:
                orderId (entier, clé primaire, auto)
                userId (entier, requis)
                référence: userId -> users.id
            """;

        assertDoesNotThrow(() -> {
            ParseTree tree = DSLParser.parse(validDSL);
            Validator validator = new Validator();
            validator.validate(tree);
        });
    }

    @Test
    public void testDuplicateTableNames() {
        String invalidDSL = """
            table users:
                id (entier, clé primaire, auto)
            table users:
                name (texte, requis)
            """;

        ParseTree tree = null;
        try {
            tree = DSLParser.parse(invalidDSL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown by DSLParser in the duplicate table names test: " + e.getMessage());
        }
        Validator validator = new Validator();

        ParseTree finalTree = tree;
        Exception exception = assertThrows(RuntimeException.class, () -> {
            validator.validate(finalTree);
        });
        assertTrue(exception.getMessage().contains("Duplicate table name: users"));
    }

    @Test
    public void testDuplicateColumnNames() {
        String invalidDSL = """
            table users:
                id (entier, clé primaire, auto)
                id (texte, requis)
            """;

        ParseTree tree = null;
        try {
            tree = DSLParser.parse(invalidDSL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown by DSLParser in the duplicate column names test: " + e.getMessage());
        }
        Validator validator = new Validator();

        ParseTree finalTree = tree;
        Exception exception = assertThrows(RuntimeException.class, () -> {
            validator.validate(finalTree);
        });
        assertTrue(exception.getMessage().contains("Duplicate column name: id"));
    }

    @Test
    public void testMissingForeignKeyReference() {
        String invalidDSL = """
            table orders:
                orderId (entier, clé primaire, auto)
                userId (entier, requis)
                référence: userId -> users.id
            """;

        ParseTree tree = null;
        try {
            tree = DSLParser.parse(invalidDSL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown by DSLParser in the foreign key test: " + e.getMessage());
        }
        Validator validator = new Validator();

        ParseTree finalTree = tree;
        Exception exception = assertThrows(RuntimeException.class, () -> {
            validator.validate(finalTree);
        });
        assertTrue(exception.getMessage().contains("Foreign key references a non-existent table: users"));
    }

    @Test
    public void testConflictingConstraints() {
        String invalidDSL = """
            table users:
                id (entier, clé primaire, auto, par défaut: 1)
            """;

        ParseTree tree = null;
        try {
            tree = DSLParser.parse(invalidDSL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception thrown by DSLParser in the conflicting constraints test: " + e.getMessage());
        }
        Validator validator = new Validator();

        ParseTree finalTree = tree;
        Exception exception = assertThrows(RuntimeException.class, () -> {
            validator.validate(finalTree);
        });
        assertTrue(exception.getMessage().contains("Column cannot have both 'auto' and 'par défaut' constraints"));
    }

    @Test
    public void testEmptyDSL() {
        String emptyDSL = "";

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ParseTree tree = DSLParser.parse(emptyDSL);
            Validator validator = new Validator();
            validator.validate(tree);
        });
        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    public void testMultipleForeignKeysReferencingSameTable() {
        String validDSL = """
            table orders:
                orderId (entier, clé primaire, auto)
                userId (entier, requis)
                référence: userId -> users.id
                adminId (entier, requis)
                référence: adminId -> users.id
            table users:
                id (entier, clé primaire, auto)
            """;

        assertDoesNotThrow(() -> {
            ParseTree tree = DSLParser.parse(validDSL);
            Validator validator = new Validator();
            validator.validate(tree);
        });
    }

    @Test
    public void testValidForeignKey() {
        String validDSL = """
            table orders:
                orderId (entier, clé primaire, auto)
                userId (entier, requis)
                référence: userId -> users.id
            table users:
                id (entier, clé primaire, auto)
            """;

        assertDoesNotThrow(() -> {
            ParseTree tree = DSLParser.parse(validDSL);
            Validator validator = new Validator();
            validator.validate(tree);
        });
    }
}
