package org.parser.core.nodes;

import org.parser.token.Token;

public abstract class Expr implements VisitableExpr {
    @SuppressWarnings("unchecked")
    @Override
    abstract public <R, A> A accept(Visitor<R, A> visitor, A... params);

    public static class LiteralExpr extends Expr {
        private final Object value;

        public LiteralExpr(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class UnaryExpr extends Expr {
        private final Token operation;
        private final Expr right;

        public UnaryExpr(Token operation, Expr right) {
            this.operation = operation;
            this.right = right;
        }

        public Token getOperation() {
            return operation;
        }

        public Expr getRight() {
            return right;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class BinaryExpr extends Expr {
        private final Expr left;
        private final Token operation;
        private final Expr right;

        public BinaryExpr(Expr left, Token operation, Expr right) {
            this.left = left;
            this.operation = operation;
            this.right = right;
        }

        public Expr getLeft() {
            return left;
        }

        public Token getOperation() {
            return operation;
        }

        public Expr getRight() {
            return right;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class GroupingExpr extends Expr {
        private final Expr expression;

        public GroupingExpr(Expr expression) {
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

    public static class VariableExpr extends Expr {
        private final Token name;

        public VariableExpr(Token name) {
            this.name = name;
        }

        public Token getName() {
            return name;
        }

        @SafeVarargs
        @Override
        public final <R, A> A accept(Visitor<R, A> visitor, A... params) {
            return visitor.visit(this, params);
        }
    }

    public static class AssignExpr extends Expr {
        private final Token name;
        private final Expr value;

        public AssignExpr(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        public Token getName() {
            return name;
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
        A visit(Expr.LiteralExpr expr, A... params);

        A visit(Expr.UnaryExpr expr, A... params);

        A visit(Expr.BinaryExpr expr, A... params);

        A visit(Expr.GroupingExpr expr, A... params);

        A visit(Expr.VariableExpr expr, A... params);

        A visit(Expr.AssignExpr expr, A... params);
    }

}
