package org.parser.core.nodes;

public interface VisitableStmt {
    @SuppressWarnings("unchecked")
    <R, A> A accept(Stmt.Visitor<R, A> visitor, A... params);
}
