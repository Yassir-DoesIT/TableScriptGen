package com.example.dsl;

import com.example.antlr.SQLGeneratorParser;
import org.antlr.v4.runtime.tree.ParseTree;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SQLGenerator {

    public String generate(ParseTree tree) {
        StringBuilder sqlScript = new StringBuilder();

        for (int i = 0; i < tree.getChildCount(); i++) {
            ParseTree child = tree.getChild(i);
            if (child instanceof SQLGeneratorParser.TableDefContext) {
                String tableSQL = generateTableSQL((SQLGeneratorParser.TableDefContext) child);
                sqlScript.append(tableSQL).append("\n\n");
            }
        }

        return sqlScript.toString().trim();
    }

    private String generateTableSQL(SQLGeneratorParser.TableDefContext ctx) {
        String tableName = ctx.tableName().getText();
        List<String> columnDefinitions = new ArrayList<>();
        List<String> foreignKeys = new ArrayList<>();

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof SQLGeneratorParser.ColumnDefContext) {
                String columnSQL = generateColumnSQL((SQLGeneratorParser.ColumnDefContext) child);
                columnDefinitions.add(columnSQL);
            } else if (child instanceof SQLGeneratorParser.ForeignKeyDefContext) {
                String foreignKeySQL = generateForeignKeySQL((SQLGeneratorParser.ForeignKeyDefContext) child);
                foreignKeys.add(foreignKeySQL);
            }
        }

        // Combine columns and foreign keys
        String combinedDefinitions = String.join(",\n    ", columnDefinitions);
        if (!foreignKeys.isEmpty()) {
            combinedDefinitions += ",\n    " + String.join(",\n    ", foreignKeys);
        }

        return String.format("CREATE TABLE %s (\n    %s\n);", tableName, combinedDefinitions);
    }

    private String generateColumnSQL(SQLGeneratorParser.ColumnDefContext ctx) {
        String columnName = ctx.columnName().getText();
        String dataType = mapDataType(ctx.type().getText());
        List<String> constraints = new ArrayList<>();

        for (SQLGeneratorParser.ConstraintContext constraintCtx : ctx.constraint()) {
            String constraint = mapConstraint(constraintCtx.getText());
            constraints.add(constraint);
        }

        return String.format("%s %s %s", columnName, dataType, String.join(" ", constraints)).trim();
    }

    private String generateForeignKeySQL(SQLGeneratorParser.ForeignKeyDefContext ctx) {
        String columnName = ctx.columnName(0).getText();
        String referencedTable = ctx.tableName().getText();
        String referencedColumn = ctx.columnName(1).getText();

        return String.format("FOREIGN KEY (%s) REFERENCES %s(%s)", columnName, referencedTable, referencedColumn);
    }

    private String mapDataType(String dslType) {
        return switch (dslType) {
            case "entier" -> "INT";
            case "texte" -> "VARCHAR(255)";
            case "date" -> "DATE";
            default -> throw new RuntimeException("Unknown data type: " + dslType);
        };
    }

    private String mapConstraint(String dslConstraint) {
        if (dslConstraint.startsWith("par défaut:")) {
            String inputDate = dslConstraint.substring(11).trim();
            return switch (inputDate){
              case "aujourd'hui" -> "DEFAULT \'" + LocalDate.now() + "\'";
              case "maintenant" -> "DEFAULT \'" + LocalDateTime.now() + "\'";
              default -> "DEFAULT '" + inputDate.substring(1,inputDate.length()-1) + "'";
            };

        }

        return switch (dslConstraint) {
            case "clé primaire" -> "PRIMARY KEY";
            case "auto" -> "AUTO_INCREMENT";
            case "requis" -> "NOT NULL";
            case "unique" -> "UNIQUE";
            default -> throw new RuntimeException("Unknown constraint: " + dslConstraint);
        };
    }

}
