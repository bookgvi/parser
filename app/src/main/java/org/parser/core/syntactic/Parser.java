package org.parser.core.syntactic;

import org.parser.token.Token;
import org.parser.token.TokenType;
import org.parser.core.nodes.Expr;
import org.parser.core.nodes.Stmt;
import org.parser.error.RuntimeError;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * program -> declaration* EOF ;
 * <p>
 * declaration -> varDeclaration | statement ;
 * varDeclaration -> "var" IDENTIFIER ('=' expression)? ';' ;
 * <p>
 * statement -> exprStmt| ifStmt | printStmt | blockStmt| whileStmt forLoopStmt ;
 * <p>
 * ifStmt -> "if" '(' expression ')' statement ("else" statement)? ;
 * exprStmt -> expression ';' ;
 * printStmt -> "print" expression ';' ;
 * blockStmt -> '{' declaration* '}'
 * whileStmt -> "while" '(' expression ')' statement ;
 * forLoopStmt -> "for" '('(varDeclaration | exprStmt)? ';' expression? ';' expression? ')' statement ;
 * <p>
 * expression -> assignment ;
 * assignment -> IDENTIFIER '=' assignemnt | logic_or ;
 * logic_or -> logic_and ("or" logic_and)* ;
 * logic_and -> equality ("and" equality)* ;
 * equality -> comparision (('==' | '!=') comparision)* ;
 * comparision -> term (('>' | '<' | '>=' | '<=') term)* ;
 * term -> factor (('+' | '-') factor)* ;
 * factor -> unary (('*' | '/') unary)* ;
 * unary -> ('!' | '-') unary | primary ;
 * primary -> NUMBER | STRING | "true" | "false" | "nill" | '(' expression ')' | IDENTIFIER ;
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
            statements.add(declaration());
        }
        return statements;
    }

    /**
     * declaration -> varDeclaration | statement ;
     * 
     * @return Statement
     */
    Stmt declaration() {
        try {
            if (match(TokenType.VAR)) {
                return varDeclaration();
            }
            return statement();
        } catch (RuntimeError re) {
            System.out.println(re.getMessage());
            synchronize();
            return null;
        }
    }

    /**
     * varDeclaration -> "var" IDENTIFIER ('=' expression)? ';' ;
     * 
     * @return Statement
     */
    Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "variable name expected");
        Expr init = null;
        if (match(TokenType.EQUAL)) {
            init = expression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after var declaration");
        return new Stmt.VarStmt(name, init);
    }

    /**
     * statement -> ifStmt | printStmt | blockStmt | exprStmt | forLoopStmt ;
     * 
     * @return Statement
     */
    Stmt statement() {
        if (match(TokenType.IF)) {
            return ifStmt();
        }
        if (match(TokenType.PRINT)) {
            return printStatemnet();
        }
        if (match(TokenType.LEFT_BRACE)) {
            return block();
        }
        if (match(TokenType.WHILE)) {
            return whileStmt();
        }
        if (match(TokenType.FOR)) {
            return forLoopStmt();
        }
        return exprStatemnet();
    }

    /**
     * forLoopStmt -> "for" '(' (varDeclaration | exprStmt)? ';' expression? ';'
     * expression? ')' statement ;
     * 
     * @return Statement
     */
    Stmt forLoopStmt() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'for'");
        Stmt initializer;
        if (match(TokenType.SEMICOLON)) {
            initializer = null;
        } else if (match(TokenType.VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = exprStatemnet();
        }

        Expr condition = null;
        if (!match(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after loop condition");

        Expr increment = null;
        if (!match(TokenType.SEMICOLON)) {
            increment = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Expected ')' after 'for' clause");

        Stmt body = statement();
        if (increment != null) {
            body = new Stmt.BlockStmt(Arrays.asList(body, new Stmt.ExprStmt(increment)));
        }
        if (condition == null) {
            condition = new Expr.LiteralExpr(true);
        }
        body = new Stmt.WhileStmt(condition, body);
        
        if (initializer != null) {
            body = new Stmt.BlockStmt(Arrays.asList(initializer, body));
        }

        return body;
    }

    /**
     * whileStmt -> "while" '(' expression ')' statement ;
     * 
     * @return Statement
     */
    Stmt whileStmt() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'while' ");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after while condition");
        Stmt body = statement();

        return new Stmt.WhileStmt(condition, body);
    }

    /**
     * ifStmt -> "if" '(' expression ')' statement ("else" statement)? ;
     * 
     * @return Statement
     */
    Stmt ifStmt() {
        consume(TokenType.LEFT_PAREN, "Expected '(' after 'if' ");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");
        Stmt thenStmt = statement();
        Stmt elseStmt = null;
        if (match(TokenType.ELSE)) {
            elseStmt = statement();
        }
        return new Stmt.IfStmt(condition, thenStmt, elseStmt);
    }

    /**
     * block -> '{' declaration* '}'
     * 
     * @return Statement;
     */
    Stmt block() {
        List<Stmt> statements = new ArrayList<>();
        while (isNotEnd() && !check(TokenType.RIGHT_BRACE)) {
            statements.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expected '{' after block statement");
        return new Stmt.BlockStmt(statements);
    }

    /**
     * printStmt -> "print" expression ';' ;
     * 
     * @return Statement
     */
    Stmt printStatemnet() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new Stmt.PrintStmt(expr);
    }

    /**
     * exprStmt -> expression ';' ;
     * 
     * @return Statement
     */
    Stmt exprStatemnet() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Expected ';'");
        return new Stmt.ExprStmt(expr);
    }

    Expr expression() {
        return assignment();
    }

    /**
     * assignment -> IDENTIFIER '=' assignment | logic_or ;
     * 
     * @return Expression
     */
    Expr assignment() {
        Expr expr = logic_or();
        if (match(TokenType.EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.VariableExpr) {
                Token name = ((Expr.VariableExpr) expr).getName();
                return new Expr.AssignExpr(name, value);
            }
            throw new RuntimeError(equals, "Invalid assignment target.");
        }
        return expr;
    }

    /**
     * logic_or -> logic_and ("or" logic_and)* ;
     * 
     * @return Expression
     */
    Expr logic_or() {
        Expr expr = logic_and();
        while (match(TokenType.OR)) {
            Token operation = previous();
            Expr right = logic_and();
            expr = new Expr.LogicalExpr(expr, operation, right);
        }
        return expr;
    }

    /**
     * logic_and -> equality ("or" equality)* ;
     * 
     * @return Expression
     */
    Expr logic_and() {
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token operation = previous();
            Expr right = equality();
            expr = new Expr.LogicalExpr(expr, operation, right);
        }
        return expr;
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
            expr = new Expr.BinaryExpr(expr, operation, right);
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
            expr = new Expr.BinaryExpr(expr, operation, right);
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
            expr = new Expr.BinaryExpr(expr, operation, right);
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
            expr = new Expr.BinaryExpr(expr, operation, right);
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
     * primary -> NUMBER | STRING | "true" | "false" | "nil" | '(' expression ')' |
     * IDENTIFIER ;
     * 
     * @return Expression;
     */
    Expr primary() {
        if (match(TokenType.TRUE))
            return new Expr.LiteralExpr(true);
        if (match(TokenType.FALSE))
            return new Expr.LiteralExpr(false);
        if (match(TokenType.NUMBER, TokenType.STRING))
            return new Expr.LiteralExpr(previous().getValue());
        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected ')'");
            return new Expr.GroupingExpr(expr);
        }
        if (match(TokenType.IDENTIFIER))
            return new Expr.VariableExpr(previous());
        return null;
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

    Token synchronize() {
        Token token = advance();
        return token;
    }
}
