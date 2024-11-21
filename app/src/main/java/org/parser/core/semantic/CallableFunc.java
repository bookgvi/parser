package org.parser.core.semantic;

import java.util.List;

public interface CallableFunc {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
