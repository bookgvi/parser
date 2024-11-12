package org.parser.core.semantic;

import java.util.Map;
import java.util.HashMap;

import org.parser.error.RuntimeError;
import org.parser.token.Token;

public class Environment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Environment enclosing;

    
    public Environment() {
        this(null);
	}


	public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object val) {
        if (!variables.containsKey(name)) {
            variables.put(name, val);
            return;
        }
        throw new RuntimeError("Variable " + name + " already defined");
    }

    public void assign(Token token, Object value) {
        if (variables.containsKey(token.getLexeme())) {
            variables.put(token.getLexeme(), value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(token, value);
            return;
        }
        throw new RuntimeError("Variable " + token.getLexeme() + " must be defined first");
    }

    public Object get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        return null;
    }
}
