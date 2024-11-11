package org.parser.error;

import org.parser.token.Token;

public class RuntimeError extends RuntimeException {
    public RuntimeError(Token token, String text) {
        super(String.format("Error occured at line %d (%s): %s", token.getLine(), token.getLexeme(), text));
    }
}
