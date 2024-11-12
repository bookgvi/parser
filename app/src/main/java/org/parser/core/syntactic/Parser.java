package org.parser.core.syntactic;

import org.parser.token.Token;
import org.parser.token.TokenType;
import org.parser.core.nodes.Expr;
import org.parser.core.nodes.Stmt;
import org.parser.error.RuntimeError;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * program -> statement* EOF ;
 * <p>
 * statement -> exprStmt | printStmt ;
 * exprStmt -> expression ';' ;
 * printStmt -> "print" expression ';' ;
 * <p>
 * expression -> equality* ;
 * equality -> comparision (('==' | '!=') comparision)* ;
 * comparision -> term (('>' | '<' | '>=' | '<=') term)* ;
 * term -> factor (('+' | '-') factor)* ;
 * factor -> unary (('*' | '/') unary)* ;
 * unary -> ('!' | '-') unary | primary ;
 * primary -> NUMBER | STRING | "true" | "false" | "nill" | '(' expression ')' ;
 */
public class Parser {
    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) {
        tokens = Optional.ofNullable(tokens).orElse(new ArrayList<>());
        this.tokens = new ArrayList<>(tokens);
        this.current = 0;
    }

    public List<Expr> parseExpr() {
        List<Expr> expressions = new ArrayList<>();
        while (isNotEnd()) {
            try {
                Expr expr = expression();
                expressions.add(expr);
                consume(TokenType.SEMICOLON, "Expected ';'");
            } catch (RuntimeError re) {
                System.out.println(re.getMessage());
            }
        }
        return expressions;
    }

    public List<Stmt> parseStmt() {
        List<Stmt> statements = new ArrayList<>();
        while (isNotEnd()) {
            try {
                statements.add(statement());
            } catch (RuntimeError re) {
                System.out.println(re.getMessage());
            }
        }
        return statements;
    }

    Stmt statement() {
        if (match(TokenType.PRINT)) {
            return printStatemnet();
        }
        return exprStatemnet();
    }

    Stmt printStatemnet() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new Stmt.PrintStmt(expr);
    }
    
    Stmt exprStatemnet() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new Stmt.ExprStmt(expr);
    }

    Expr expression() {
        return equality();
    }

    /**
     * equality -> comparision (('==' | '!=') comparision)* ;
     * 
     * @return Expression
     */
    Expr equality() {
        Expr expr = comparision();
        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operation = previous();
            Expr right = comparision();
            return new Expr.BinaryExpr(expr, operation, right);
        }
        return expr;
    }

    /**
     * comparision -> term (('>' | '>=' | '<' | '<=') term)* ;
     * 
     * @return Expression
     */
    Expr comparision() {
        Expr expr = term();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            Token operation = previous();
            Expr right = term();
            return new Expr.BinaryExpr(expr, operation, right);
        }
        return expr;
    }

    /**
     * term -> factor (('+' | '-') factor)* ;
     * 
     * @return Expression
     */
    Expr term() {
        Expr expr = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operation = previous();
            Expr right = factor();
            return new Expr.BinaryExpr(expr, operation, right);
        }
        return expr;
    }

    /**
     * factor -> unary (('*' | '/') unary)* ;
     * 
     * @return Expression
     */
    Expr factor() {
        Expr expr = unary();
        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operation = previous();
            Expr right = unary();
            return new Expr.BinaryExpr(expr, operation, right);
        }
        return expr;
    }

    /**
     * unary -> ('!' | '-' | '+') unary | primary ;
     * 
     * @return Expression;
     */
    Expr unary() {
        while (match(TokenType.BANG, TokenType.MINUS, TokenType.PLUS)) {
            Token operation = previous();
            Expr right = unary();
            return new Expr.UnaryExpr(operation, right);
        }
        return primary();
    }

    /**
     * primary -> NUMBER | STRING | "true" | "false" | "nil" | '(' expression ')' ;
     * 
     * @return Expression;
     */
    Expr primary() {
        if (match(TokenType.TRUE)) return new Expr.LiteralExpr(true);
        if (match(TokenType.FALSE)) return new Expr.LiteralExpr(false);
        if (match(TokenType.NUMBER, TokenType.STRING)) return new Expr.LiteralExpr(previous().getValue());
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_BRACE, "Expected ')'");
            return new Expr.GroupingExpr(expr);
        }
        return new Expr.LiteralExpr(null);
    }

    Token consume(TokenType type, String msg) {
        if (!check(type)) {
            throw new RuntimeError(peek(), msg);
        }
        return advance();
    }

    boolean match(TokenType... kinds) {
        for (TokenType kind : kinds) {
            if (isNotEnd() && check(kind)) {
                advance();
                return true;
            }
        }
        return false;
    }

    boolean check(TokenType type) {
        return isNotEnd() && type == peek().getKind();
    }

    Token peek() {
        return tokens.get(current);
    }

    Token advance() {
        if (isNotEnd()) {
            return tokens.get(current++);
        }
        return previous();
    }

    Token previous() {
        if (current > 0) {
            return tokens.get(current - 1);
        }
        throw new RuntimeError("Unsupported token access");
    }

    boolean isNotEnd() {
        return current < tokens.size() && tokens.get(current).getKind() != TokenType.EOF;
    }
}
