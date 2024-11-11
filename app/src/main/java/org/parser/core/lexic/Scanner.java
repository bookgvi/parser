package org.parser.core.lexic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.parser.error.RuntimeError;
import org.parser.token.Token;
import org.parser.token.TokenType;

public class Scanner {
    private final char CTRL_Z = '\u001a';
    private final List<Token> tokens;
    private final char[] chars;
    private int start;
    private int current;
    private int line;

    public Scanner(char[] chars) {
        this.chars = Optional.ofNullable(chars).orElse(new char[0]);
        this.tokens = new ArrayList<>();
        this.line = 1;
    }

    public List<Token> scan() {
        while (isNotEnd()) {
            this.start = current;
            try {
                parse();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        addToken(TokenType.EOF, "", null, line);
        return tokens;
    }

    void parse() {
        char ch = advance();
        switch (ch) {
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case '/': addToken(TokenType.SLASH); break;
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '.': addToken(TokenType.DOT); break;
            case ',': addToken(TokenType.COMMA); break;
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '"': string(); break;
            case '\t':
            case '\r':
            case ' ': break;
            case '\n': line += 1; break;
            default:
                if (Character.isDigit(ch)) {
                    numeric();
                } else if (Character.isJavaIdentifierStart(ch)) {
                    identifier();
                } else {
                    String lexeme = new String(chars, start, current - start);
                    Token token = Token.builder().lexeme(lexeme).kind(TokenType.NIL).line(line).build();
                    throw new RuntimeError(token, "Unknown character");
                }
            break;
        }
    }

    void identifier() {
        while (isNotEnd() && Character.isJavaIdentifierPart(peek())) {
            advance();
        }
        String lexeme = new String(chars, start, current - start);
        TokenType kind = TokenType.getTypesMap().getOrDefault(lexeme, TokenType.IDENTIFIER);
        addToken(kind, lexeme, null, line);
    }

    void numeric() {
        while (isNotEnd() && Character.isDigit(peek())) {
            advance();
        }
        if (check('.') && Character.isDigit(peekNext())) {
            do {
                advance();
            } while (isNotEnd() && Character.isDigit(peek()));
        }
        String lexeme = new String(chars, start, current - start);
        Double value = Double.valueOf(lexeme);
        addToken(TokenType.NUMBER, lexeme, value, line);
    }

    void string() {
        do {
            if (check('\n')) {
                line += 1;
            }
            advance();
        } while (isNotEnd() && !check('"'));
        advance();
        String lexeme = new String(chars, start, current - start);
        String value = new String(chars, start - 1, current - start);
        Token token = addToken(TokenType.STRING, lexeme, value, line);
        if (!isNotEnd()) {
            throw new RuntimeError(token, "Unclosed string");
        }
    }

    char advance() {
        return isNotEnd() ? chars[current++] : CTRL_Z;
    }

    boolean match(char ch) {
        if (check(ch)) {
            ++current;
            return true;
        }
        return false;
    }

    boolean check(char ch) {
        return ch == peek();
    }

    char peek() {
        return isNotEnd() ? chars[current] : CTRL_Z;
    }

    char peekNext() {
        return chars.length > current + 1 ? chars[current + 1] : CTRL_Z;
    }

    char previous() {
        return current > 0 && chars.length > current ? chars[current - 1] : CTRL_Z;
    }

    Token addToken(TokenType type) {
        String lexeme = new String(chars, start, current - start);
        return addToken(type, lexeme, null, line);
    }

    Token addToken(TokenType type, String lexeme, Object value, int line) {
        Token token = Token.builder().lexeme(lexeme).kind(type).value(value).line(line).build();
        tokens.add(token);
        return token;
    }

    boolean isNotEnd() {
        return current < chars.length;
    }
}
