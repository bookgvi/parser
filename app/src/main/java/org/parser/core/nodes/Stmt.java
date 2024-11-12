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

    @SuppressWarnings("unchecked")
    public static interface Visitor<R, A> {
        A visit(Stmt.ExprStmt stmt, A... params);

        A visit(Stmt.PrintStmt stmt, A... params);

        A visit(Stmt.VarStmt stmt, A... params);

        A visit(Stmt.BlockStmt stmt, A... params);
    }
}
