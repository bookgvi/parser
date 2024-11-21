package org.parser.core.semantic;

import java.util.List;

import org.parser.core.nodes.Stmt;
import org.parser.core.nodes.Stmt.FuncStmt;
import org.parser.error.Return;

public class FunctionFunc implements CallableFunc {

    private final Stmt.FuncStmt funcStmt;
    
    public FunctionFunc(FuncStmt funcStmt) {
        this.funcStmt = funcStmt;
    }

    
    @Override
    public int arity() {
        return funcStmt.getParams().size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.getGlobal());
        for (int i = 0; i < arity(); ++i) {
            environment.define(funcStmt.getParams().get(i).getLexeme(), arguments.get(i));
        }
        try {
            interpreter.executeBlock(funcStmt.getBody(), environment, new Object[0]);
        } catch (Return ret) {
            return ret.getValue();
        }
        return null;
    }


    public Stmt.FuncStmt getFuncStmt() {
        return funcStmt;
    }
    
}
