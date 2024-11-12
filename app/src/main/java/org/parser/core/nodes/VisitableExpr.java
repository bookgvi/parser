package org.parser.core.nodes;

@SuppressWarnings("unchecked")
public interface VisitableExpr {
	<R, A> A accept(Expr.Visitor<R, A> visitor, A... params);
}
