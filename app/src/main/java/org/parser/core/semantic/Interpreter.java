package org.parser.core.semantic;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import org.parser.core.nodes.Expr;
import org.parser.core.nodes.Expr.AssignExpr;
import org.parser.core.nodes.Expr.BinaryExpr;
import org.parser.core.nodes.Expr.CallExpr;
import org.parser.core.nodes.Expr.GroupingExpr;
import org.parser.core.nodes.Expr.LiteralExpr;
import org.parser.core.nodes.Expr.LogicalExpr;
import org.parser.core.nodes.Expr.PostfixOpExpr;
import org.parser.core.nodes.Expr.PrefixOpExpr;
import org.parser.core.nodes.Expr.UnaryExpr;
import org.parser.core.nodes.Expr.VariableExpr;
import org.parser.core.nodes.Stmt;
import org.parser.core.nodes.Stmt.BlockStmt;
import org.parser.core.nodes.Stmt.ExprStmt;
import org.parser.core.nodes.Stmt.FuncStmt;
import org.parser.core.nodes.Stmt.IfStmt;
import org.parser.core.nodes.Stmt.PrintStmt;
import org.parser.core.nodes.Stmt.ReturnStmt;
import org.parser.core.nodes.Stmt.VarStmt;
import org.parser.core.nodes.Stmt.WhileStmt;
import org.parser.error.Return;
import org.parser.error.RuntimeError;
import org.parser.token.Token;
import org.parser.token.TokenType;

public class Interpreter implements Expr.Visitor<Object, Object>, Stmt.Visitor<Object, Object> {

    private final Environment global = new Environment();
    private Environment env = global;

    public void interpret(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            try {
                execute(stmt, new Object[0]);
            } catch (RuntimeError re) {
                System.out.println(re.getMessage());
            }
        }
    }

    public Object execute(Stmt stmt, Object... params) {
        return stmt.accept(this, params);
    }

    public Object evaluate(Expr expr, Object... params) {
        return expr.accept(this, params);
    }

    public void executeBlock(List<Stmt> statements, Environment environment, Object... params) {
        Environment prev = env;
        try {
            env = environment;
            for (Stmt statement : statements) {
                execute(statement, params);
            }
        } finally {
            env = prev;
        }
    }

    @Override
    public Object visit(ExprStmt stmt, Object... params) {
        return evaluate(stmt.getExpression(), params);
    }

    @Override
    public Object visit(PrintStmt stmt, Object... params) {
        Object value = evaluate(stmt.getExpression(), params);
        if (value instanceof Double) {
            String v = String.valueOf(value);
            if (v.endsWith(".0")) {
                v = v.substring(0, v.length() - 2);
                System.out.println(v);
                return null;
            }
        }
        System.out.println(value);
        return null;
    }

    @Override
    public Object visit(VarStmt stmt, Object... params) {
        Object value = evaluate(stmt.getInitializer(), params);
        Token name = stmt.getName();
        env.define(name.getLexeme(), value);
        return null;
    }

    @Override
    public Object visit(BlockStmt stmt, Object... params) {
        executeBlock(stmt.getStatements(), new Environment(env), params);
        return null;
    }

    @Override
    public Object visit(LiteralExpr expr, Object... params) {
        return expr.getValue();
    }

    @Override
    public Object visit(UnaryExpr expr, Object... params) {
        Object value = evaluate(expr.getRight(), params);
        Token operation = expr.getOperation();
        return switch (operation.getKind()) {
            case BANG -> !isTruthy(value);
            case MINUS -> {
                if (isNumber(value)) {
                    yield -((double) value);
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case PLUS -> {
                if (isNumber(value)) {
                    yield ((double) value);
                }
                throw new RuntimeError("Type cast exceptions");
            }
            default -> null;
        };
    }

    @Override
    public Object visit(BinaryExpr expr, Object... params) {
        Object leftVal = evaluate(expr.getLeft(), params);
        Object rightVal = evaluate(expr.getRight(), params);
        Token operation = expr.getOperation();
        return switch (operation.getKind()) {
            case PLUS -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal + (double) rightVal;
                } else if (isString(leftVal)) {
                    yield String.valueOf(leftVal) + String.valueOf(rightVal);
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case MINUS -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal - (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case STAR -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal * (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case SLASH -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal / (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case LESS -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal < (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case GREATER -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal > (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case GREATER_EQUAL -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal >= (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case LESS_EQUAL -> {
                if (isNumber(leftVal) && isNumber(rightVal)) {
                    yield (double) leftVal <= (double) rightVal;
                }
                throw new RuntimeError("Type cast exceptions");
            }
            case EQUAL_EQUAL -> isEqual(leftVal, rightVal);
            case BANG_EQUAL -> !isEqual(leftVal, rightVal);
            default -> null;
        };
    }

    @Override
    public Object visit(GroupingExpr expr, Object... params) {
        return evaluate(expr.getExpression(), params);
    }

    @Override
    public Object visit(VariableExpr expr, Object... params) {
        return env.get(expr.getName().getLexeme());
    }

    @Override
    public Object visit(AssignExpr expr, Object... params) {
        Object value = evaluate(expr.getValue());
        env.assign(expr.getName(), value);
        return value;
    }

    @Override
    public Object visit(IfStmt stmt, Object... params) {
        if (isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getThenStmt(), params);
        } else if (stmt.getElseStmt() != null) {
            execute(stmt.getElseStmt(), params);
        }
        return null;
    }

    @Override
    public Object visit(LogicalExpr expr, Object... params) {
        Object leftVal = evaluate(expr.getLeft());
        if (expr.getOperation().getKind() == TokenType.OR) {
            if (isTruthy(leftVal)) {
                return leftVal;
            }
        } else {
            if (!isTruthy(leftVal)) {
                return leftVal;
            }
        }
        return evaluate(expr.getRight(), params);
    }

    @Override
    public Object visit(WhileStmt stmt, Object... params) {
        Object condition = evaluate(stmt.getConfition(), params);
        while (isTruthy(condition)) {
            execute(stmt.getBody(), params);
            condition = evaluate(stmt.getConfition(), params);
        }
        return null;
    }

    @Override
    public Object visit(PrefixOpExpr expr, Object... params) {
        Token operation = expr.getOperation();
        Object value = env.get(expr.getVariable().getLexeme());
        if (!isNumber(value)) {
            throw new RuntimeError(expr.getVariable(), "Value must be a number");
        }
        return switch (operation.getKind()) {
            case INCREMENT -> {
                Double res = (Double) value + 1;
                env.assign(expr.getVariable(), res);
                yield res;
            }
            case DECREMENT -> {
                Double res = (Double) value - 1;
                env.assign(expr.getVariable(), res);
                yield res;
            }
            default -> null;
        };
    }

    @Override
    public Object visit(PostfixOpExpr expr, Object... params) {
        Token operation = expr.getOperation();
        Object value = env.get(expr.getVariable().getLexeme());
        if (!isNumber(value)) {
            throw new RuntimeError(expr.getVariable(), "Value must be a number");
        }
        return switch (operation.getKind()) {
            case INCREMENT -> {
                Double res = (Double) value + 1;
                env.assign(expr.getVariable(), res);
                yield value;
            }
            case DECREMENT -> {
                Double res = (Double) value - 1;
                env.assign(expr.getVariable(), res);
                yield value;
            }
            default -> null;
        };
    }

    @Override
    public Object visit(CallExpr expr, Object... params) {
        Object callee = evaluate(expr.getCallee());
        List<Object> arguments = new ArrayList<>();
        for (Expr argExpr : expr.getArguments()) {
            arguments.add(evaluate(argExpr, params));
        }
        if (!(callee instanceof CallableFunc func)) {
            throw new RuntimeError("Not a function");
        }
        if (arguments.size() != func.arity()) {
            throw new RuntimeError("Expected " + func.arity() + " arguments but got " + arguments.size() + ".");
        }
        return func.call(this, arguments);
    }


    @Override
    public Object visit(FuncStmt stmt, Object... params) {
        FunctionFunc func = new FunctionFunc(stmt);
        env.define(stmt.getName().getLexeme(), func);
        return null;
    }

    @Override
    public Object visit(ReturnStmt stmt, Object... params) {
        Object value = null;
        if (stmt.getValue() != null) {
            value = evaluate(stmt.getValue(), params); 
        }
        throw new Return(value);
    }
    
    boolean isNumber(Object value) {
        return value != null && value instanceof Number;
    }

    boolean isString(Object value) {
        return value != null && value instanceof String;
    }

    boolean isEqual(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    private boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (boolean) value;
        }
        return false;
    }

    public Environment getGlobal() {
        return global;
    }

    public Environment getEnv() {
        return env;
    }
}
