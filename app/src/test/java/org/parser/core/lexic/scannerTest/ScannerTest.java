package org.parser.core.lexic.scannerTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.parser.core.lexic.Scanner;
import org.parser.token.Token;

public class ScannerTest {
    @Test
    public void simpleTestTokenScanner() throws IOException {
        File file = new File("/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/lexic/scannerTest/class.clazz");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            assertEquals(18, tokens.size());
        }
    }

    @Test
    public void simpleTestTokenScannerWithErrors() throws IOException {
        File file = new File("/Users/bookgvi/IdeaProjects/parser/app/src/test/java/org/parser/core/lexic/scannerTest/class_with_errors.clazz");
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            char[] chars = buffer.lines().collect(Collectors.joining("\n")).toCharArray();
            Scanner scanner = new Scanner(chars);
            List<Token> tokens = scanner.scan();
            assertEquals(21, tokens.size());
        }
    }
}