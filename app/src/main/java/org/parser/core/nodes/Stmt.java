package org.parser.core.nodes;

import java.util.ArrayList;
import java.util.List;

import org.parser.token.Token;

public abstract class Stmt implements VisitableStmt {

    @SuppressWarnings("unchecked")
    @Override
    abstract public <R, A> A accept(Visitor<R, A> visitor, A... params);

    public static class ExprStmt extends Stmt {
        private final Expr expression;

        public ExprStmt(Expr expression) {
            this.expression = expression;
        }

        public Expr getExpression() {
            return expression;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }

    }

    public static class PrintStmt extends Stmt {
        private final Expr expression;

        public PrintStmt(Expr expr) {
            this.expression = expr;
        }

        public Expr getExpression() {
            return expression;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class VarStmt extends Stmt {
        private final Token name;
        private final Expr initializer;

        public VarStmt(Token name, Expr init) {
            this.name = name;
            this.initializer = init;
        }

        public Token getName() {
            return name;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }

        public Expr getInitializer() {
            return initializer;
        }
    }

    public static class BlockStmt extends Stmt {
        private final List<Stmt> statements;

        public BlockStmt(List<Stmt> statements) {
            this.statements = new ArrayList<>(statements);
        }

        public List<Stmt> getStatements() {
            return new ArrayList<>(statements);
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }

    }

    public static class IfStmt extends Stmt {
        private final Expr condition;
        private final Stmt thenStmt;
        private final Stmt elseStmt;

        public IfStmt(Expr condition, Stmt thenStmt, Stmt elseStmt) {
            this.condition = condition;
            this.thenStmt = thenStmt;
            this.elseStmt = elseStmt;
        }

        public Expr getCondition() {
            return condition;
        }

        public Stmt getThenStmt() {
            return thenStmt;
        }

        public Stmt getElseStmt() {
            return elseStmt;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class WhileStmt extends Stmt {
        private final Expr confition;
        private final Stmt body;

        public WhileStmt(Expr confition, Stmt body) {
            this.confition = confition;
            this.body = body;
        }

        public Expr getConfition() {
            return confition;
        }

        public Stmt getBody() {
            return body;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }

    }

    public static class FuncStmt extends Stmt {
        private final Token name;
        private final List<Token> params;
        private final List<Stmt> body;

        public FuncStmt(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        public Token getName() {
            return name;
        }

        public List<Token> getParams() {
            return params;
        }

        public List<Stmt> getBody() {
            return body;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class ReturnStmt extends Stmt {
        private final Expr value;

        public ReturnStmt(Expr value) {
            this.value = value;
        }

        public Expr getValue() {
            return value;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }

    }

    @SuppressWarnings("unchecked")
    public static interface Visitor<R, A> {
        A visit(Stmt.ExprStmt stmt, A... params);

        A visit(Stmt.PrintStmt stmt, A... params);

        A visit(Stmt.VarStmt stmt, A... params);

        A visit(Stmt.BlockStmt stmt, A... params);

        A visit(Stmt.IfStmt stmt, A... params);

        A visit(Stmt.WhileStmt stmt, A... params);

        A visit(Stmt.FuncStmt stmt, A... params);

        A visit(Stmt.ReturnStmt stmt, A... params);
    }
}
