package org.parser.token;

public class Token {
    private String lexeme;
    private TokenType kind;
    private Object value;
    private int line;

    private Token(
        String lexeme,
        TokenType kind,
        Object value,
        int line
    ) {
        this.lexeme = lexeme;
        this.kind = kind;
        this.value = value;
        this.line = line;
    }

    public Token.Builder builder() {
        return new Token.Builder();
    }

    public String getLexeme() {
        return this.lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public TokenType getKind() {
        return this.kind;
    }

    public void setKind(TokenType kind) {
        this.kind = kind;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public static class Builder {
        private String lexeme;
        private TokenType kind;
        private Object value;
        private int line;
    
        public Builder lexeme(String lexeme) {
            this.lexeme = lexeme;
            return this;
        }

        public Builder kind(TokenType kind) {
            this.kind = kind;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder line(int line) {
            this.line = line;
            return this;
        }

        public Token build() {
            return new Token(lexeme, kind, value, line);
        }
    }

}
