package com.example.dsl;

import org.antlr.v4.runtime.tree.ParseTree;
import com.example.antlr.SQLGeneratorParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Validator {
    // every table should be mapped to its columns, the columns are stored in a Set
    private final Map<String, Set<String>> tables = new HashMap<>();

    // foreign keys and their references will be stored here in order to be validated later after all tables are validated
    private final List<ForeignKeyValidationTask> foreignKeyTasks = new ArrayList<>();

    // this Set is used to keep already processed foreign keys in memory in order to ensure there are no duplicates
    private final Set<String> processedForeignKeys = new HashSet<>();

    public void validate(ParseTree tree) {
        System.out.println("DEBUG: Entering validate(ParseTree).");

        visit(tree);              // populate tables and collect foreign key validation tasks
        validateDeferredForeignKeys(); // validates all foreign keys after all tables are validated
        System.out.println("DEBUG: Exiting validate(ParseTree).");
    }

    private void visit(ParseTree tree) {

        System.out.println("DEBUG: Visiting node: " + tree.getClass().getSimpleName()
                + " => text: '" + tree.getText() + "'");

        if (tree instanceof SQLGeneratorParser.TableDefContext) {
            validateTable((SQLGeneratorParser.TableDefContext) tree);
            return;
        }

        for (int i = 0; i < tree.getChildCount(); i++) {
            visit(tree.getChild(i));
        }
    }

    private void validateTable(SQLGeneratorParser.TableDefContext ctx) {
        String tableName = ctx.tableName().getText().trim();
        System.out.println("DEBUG: validateTable - Processing table: '" + tableName + "'");

        if (tables.containsKey(tableName)) {
            throw new RuntimeException("Duplicate table name: " + tableName);
        }

        Set<String> columns = new HashSet<>();

        for (ParseTree child : ctx.children) {
            System.out.println("DEBUG: validateTable - child node: " + child.getClass().getSimpleName()
                    + " => text: '" + child.getText() + "'");

            if (child instanceof SQLGeneratorParser.ColumnDefContext) {
                validateColumn((SQLGeneratorParser.ColumnDefContext) child, columns);
            }
            else if (child instanceof SQLGeneratorParser.ForeignKeyDefContext) {
                collectForeignKeyTask((SQLGeneratorParser.ForeignKeyDefContext) child, tableName);
            }
        }

        tables.put(tableName, columns);
    }

    private void validateColumn(SQLGeneratorParser.ColumnDefContext ctx, Set<String> existingColumns) {
        String columnName = ctx.columnName().getText().trim();
        String columnType = ctx.type().getText().trim();
        boolean hasDefault = false;

        System.out.println("DEBUG: validateColumn - Found column: '" + columnName + "' - Type: '" + columnType + "'");


        if (existingColumns.contains(columnName)) {
            throw new RuntimeException("Duplicate column name: " + columnName);
        }

        existingColumns.add(columnName);

        Set<String> constraints = new HashSet<>();
        for (SQLGeneratorParser.ConstraintContext constraintCtx : ctx.constraint()) {
            String constraint = constraintCtx.getText();
            System.out.println("DEBUG: validateColumn - Found constraint: '" + constraint + "'");

            if (constraint.equals("auto") && constraints.contains("par défaut")) {
                throw new RuntimeException("Column cannot have both 'auto' and 'par défaut' constraints.");
            }
            if (constraint.startsWith("par défaut") && constraints.contains("auto")) {
                throw new RuntimeException("Column cannot have both 'auto' and 'par défaut' constraints.");
            }
            if(constraint.startsWith("par défaut")){
                if(!columnType.equals("date")){
                    throw new RuntimeException("Only columns of type 'date' can have a default value. Column: '" + columnName + "'");
                }
                hasDefault = true;
                validateDefaultConstraint(constraint);
            }

            constraints.add(constraint);
        }
    }

    private void validateDefaultConstraint(String constraint) {
        String constraintInQuestion = constraint.substring(11).trim();
        if (constraintInQuestion.equals("aujourd'hui") || constraintInQuestion.equals("maintenant")) {
            return;
        }
        try {
            LocalDate.parse(constraintInQuestion.substring(1,constraintInQuestion.length()-1), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format: " + constraintInQuestion);
        }
    }

    private void collectForeignKeyTask(SQLGeneratorParser.ForeignKeyDefContext ctx, String currentTable) {
        System.out.println("DEBUG: collectForeignKeyTask - raw foreignKeyDef text: '"
                + ctx.getText() + "'");


        String referencedTable = ctx.tableName().getText().trim();

        String foreignKeyIdentifier = currentTable + "->" + referencedTable;

        if (!processedForeignKeys.add(foreignKeyIdentifier)) {
            System.out.println("DEBUG: collectForeignKeyTask - SKIPPING duplicate foreign key: "
                    + foreignKeyIdentifier);
            return;
        }

        System.out.println("Collecting foreign key: " + currentTable + " -> " + referencedTable);

        // validation needs to be deferred because all tables need to be in the tree to validate foreign key references
        foreignKeyTasks.add(new ForeignKeyValidationTask(currentTable, referencedTable));
    }

    private void validateDeferredForeignKeys() {
        Set<String> missingTables = new HashSet<>();

        for (ForeignKeyValidationTask task : foreignKeyTasks) {
            System.out.println("Validating foreign key for table '" + task.currentTable
                    + "' referencing table '" + task.referencedTable + "'.");

            if (!tables.containsKey(task.referencedTable)) {
                missingTables.add(task.referencedTable);
            }
        }

        if (!missingTables.isEmpty()) {
            if (missingTables.size() == 1) {
                String missing = missingTables.iterator().next();
                throw new RuntimeException("Foreign key references a non-existent table: " + missing);
            } else {
                throw new RuntimeException("Foreign key references non-existent tables: "
                        + String.join(", ", missingTables));
            }
        }
    }

    // Helper class for deferred foreign key validation
    private static class ForeignKeyValidationTask {
        private final String currentTable;
        private final String referencedTable;

        public ForeignKeyValidationTask(String currentTable, String referencedTable) {
            this.currentTable = currentTable;
            this.referencedTable = referencedTable;
        }
    }
}
