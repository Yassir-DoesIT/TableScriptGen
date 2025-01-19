// Generated from SQLGenerator.g4 by ANTLR 4.12.0

    package com.example.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SQLGeneratorParser}.
 */
public interface SQLGeneratorListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SQLGeneratorParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SQLGeneratorParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#tableDef}.
	 * @param ctx the parse tree
	 */
	void enterTableDef(SQLGeneratorParser.TableDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#tableDef}.
	 * @param ctx the parse tree
	 */
	void exitTableDef(SQLGeneratorParser.TableDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#columnDef}.
	 * @param ctx the parse tree
	 */
	void enterColumnDef(SQLGeneratorParser.ColumnDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#columnDef}.
	 * @param ctx the parse tree
	 */
	void exitColumnDef(SQLGeneratorParser.ColumnDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#foreignKeyDef}.
	 * @param ctx the parse tree
	 */
	void enterForeignKeyDef(SQLGeneratorParser.ForeignKeyDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#foreignKeyDef}.
	 * @param ctx the parse tree
	 */
	void exitForeignKeyDef(SQLGeneratorParser.ForeignKeyDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(SQLGeneratorParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(SQLGeneratorParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#constraint}.
	 * @param ctx the parse tree
	 */
	void enterConstraint(SQLGeneratorParser.ConstraintContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#constraint}.
	 * @param ctx the parse tree
	 */
	void exitConstraint(SQLGeneratorParser.ConstraintContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(SQLGeneratorParser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(SQLGeneratorParser.DefaultValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#tableName}.
	 * @param ctx the parse tree
	 */
	void enterTableName(SQLGeneratorParser.TableNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#tableName}.
	 * @param ctx the parse tree
	 */
	void exitTableName(SQLGeneratorParser.TableNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link SQLGeneratorParser#columnName}.
	 * @param ctx the parse tree
	 */
	void enterColumnName(SQLGeneratorParser.ColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link SQLGeneratorParser#columnName}.
	 * @param ctx the parse tree
	 */
	void exitColumnName(SQLGeneratorParser.ColumnNameContext ctx);
}