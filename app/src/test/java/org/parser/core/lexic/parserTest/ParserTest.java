package org.parser.core.lexic.parserTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.parser.core.lexic.Scanner;
import org.parser.core.nodes.Expr;
import org.parser.core.nodes.Stmt;
import org.parser.token.Token;
import org.parser.core.syntactic.Parser;

public class ParserTest {
    @Test
    public void simpleTestTokenParser() throws IOException {
        File file = new File("/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/lexic/parserTest/class.clazz");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            assertEquals(18, tokens.size());
            Parser parser = new Parser(tokens);
            List<Expr> expressions = parser.parseExpr();
            assertFalse(expressions.isEmpty());
            assertEquals(4, expressions.size());
        }
    }

    @Test
    public void simpleTestTokenParserWithErrors() throws IOException {
        File file = new File("/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/lexic/parserTest/class_with_errors.clazz");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            assertEquals(21, tokens.size());
            Parser parser = new Parser(tokens);
            List<Expr> expressions = parser.parseExpr();
            assertFalse(expressions.isEmpty());
            assertEquals(6, expressions.size());
        }
    }

    @Test
    public void simpleTestTokenParserForString() throws IOException {
        File file = new File("/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/lexic/parserTest/strExpr.test");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            Parser parser = new Parser(tokens);
            List<Expr> expressions = parser.parseExpr();
            assertFalse(expressions.isEmpty());
            assertEquals(2, expressions.size());
        }
    }

    @Test
    public void simpleTestTokenParserForStmts() throws IOException {
        File file = new File("/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/lexic/parserTest/classWithStmts.clazz");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parseStmt();
            assertFalse(statements.isEmpty());
            assertEquals(4, statements.size());
            assertTrue(statements.get(0) instanceof Stmt.PrintStmt);
            assertTrue(statements.get(1) instanceof Stmt.ExprStmt);
            assertTrue(statements.get(2) instanceof Stmt.ExprStmt);
            assertTrue(statements.get(3) instanceof Stmt.PrintStmt);
        }
    }
}