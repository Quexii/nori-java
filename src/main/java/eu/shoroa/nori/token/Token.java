package eu.shoroa.nori.token;

import java.nio.CharBuffer;

public class Token {
    public final TokenType type;
    public final int start, len, line, column;
    public final CharBuffer buffer;

    public Token(TokenType type, int start, int len, int line, int column, CharBuffer buffer) {
        this.type = type;
        this.start = start;
        this.len = len;
        this.line = line;
        this.column = column;
        this.buffer = buffer;
    }
}
