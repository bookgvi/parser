package org.parser.core.nodes;

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

    @SuppressWarnings("unchecked")
    public static interface Visitor<R, A> {
        A visit(Stmt.ExprStmt stmt, A... params);
        A visit(Stmt.PrintStmt stmt, A... params);
    }
}