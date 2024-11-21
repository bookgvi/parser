package org.parser.error;

public class Return extends RuntimeException {
    private final Object value;

    public Return(Object value) {
        super();
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

}
