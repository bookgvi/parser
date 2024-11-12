package org.parser.core.nodes;

public interface VisitableExpr {
    <R, A> A accept(Expr.Visitor<R, A> visitor, A... params);
}
