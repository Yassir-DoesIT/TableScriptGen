package com.example.dsl;

import com.example.antlr.SQLGeneratorLexer;
import com.example.antlr.SQLGeneratorParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class DSLParser {

    public static ParseTree parse(String dslInput) throws Exception {
        CharStream input = CharStreams.fromString(dslInput);
        SQLGeneratorLexer lexer = new SQLGeneratorLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SQLGeneratorParser parser = new SQLGeneratorParser(tokens);

        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new RuntimeException("Syntax error at line " + line + ":" + charPositionInLine + " - " + msg);
            }
        });

        return parser.program();
    }
}
