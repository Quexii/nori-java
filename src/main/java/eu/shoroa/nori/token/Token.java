package eu.shoroa.nori.token;

import java.nio.CharBuffer;

public class Token {
    public final TokenType type;
    public final int line, column;
    public final CharBuffer buffer;

    public Token(TokenType type, int line, int column, CharBuffer buffer) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.buffer = buffer;
    }
}
