package org.parser.core.semantic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.parser.core.lexic.Scanner;
import org.parser.core.nodes.Stmt;
import org.parser.core.syntactic.Parser;
import org.parser.token.Token;

public class ControlFlowTest {
       @Test
    public void simpleTestForInterpreter() throws IOException {
        File file = new File(
                "/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/semantic/interpretterTest/testControlFlow.clazz");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            Parser parser = new Parser(tokens);
            List<Stmt> statements = parser.parseStmt();
            Interpreter interpreter = new Interpreter();
            interpreter.interpret(statements);
            System.out.println("FIN");
        }
    }
}
