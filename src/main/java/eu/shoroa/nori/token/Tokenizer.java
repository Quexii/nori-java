package eu.shoroa.nori.token;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private String str;
    private int pos, line, column;

    private char peek(int offset) {
        if (str == null) {
            throw new IllegalStateException("Tokenizer has not been initialized with input string.");
        }

        int index = pos + offset;
        if (index < 0 || index >= str.length()) {
            throw new IndexOutOfBoundsException("Offset [" + offset + "] is out of bounds for the input string of length [" + str.length() + "] for index [" + index + "]. (l: " + line + ", c: " + column + ")");
        }
        return str.charAt(index);
    }

    private char peek() {
        return peek(0);
    }

    private void advance() {
        char c = peek();

        if (c == '\0') {
            return;
        }

        pos++;

        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
    }

    private void skipIgnored() {
        boolean skipped;

        do {
            skipped = false;
            while (Character.isSpaceChar(peek()) || Character.isWhitespace(peek())) {
                advance();
                skipped = true;
            }

            if (peek() == '/' && peek(1) == '/') {
                skipped = true;

                while (peek() != '\n' && peek() != '\0') advance();
            }

            if (peek() == '/' && peek(1) == '*') {
                skipped = true;

                advance();
                advance();

                while (!(peek() == '*' && peek(1) == '/')) {
                    if (peek() == '\0')
                        throw new RuntimeException("Unterminated comment at line " + line + ", column " + column);

                    advance();
                }

                advance();
                advance();
            }
        } while (skipped);
    }

    private static boolean isAlpha(char c) {
        return Character.isAlphabetic(c) || c == '_';
    }

    private static boolean isAlNum(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
    }

    private static boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isNext() {
        char c = peek();

        if (c == '"' || c == '\0') {
            return false;
        }

        if (c == '\\') {
            advance();
            if (peek() == '\0') {
                advance();
            }
            return true;
        }

        advance();
        return true;
    }

    private static boolean isHexDigit(char c) {
        return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }

    private Token makeNumberToken(int start) {
        CharBuffer buf = CharBuffer.wrap(str, start, pos).slice();
        return new Token(TokenType.NUMBER, line, column, buf);
    }

    private Token scanIdentifier() {
        final int start = pos;

        while (isAlNum(peek())) advance();

        final int len = pos - start;

        final TokenType tokenType;
        if (len == 4 && str.regionMatches(start, "true", 0, 4)) {
            tokenType = TokenType.BOOL;
        } else if (len == 5 && str.regionMatches(start, "false", 0, 5)) {
            tokenType = TokenType.BOOL;
        } else tokenType = TokenType.IDENTIFIER;

        CharBuffer buf = CharBuffer.wrap(str, start, pos).slice();
        return new Token(tokenType, line, column, buf);
    }

    private Token scanLink() {
        advance();
        final int start = pos;

        while (isAlNum(peek()) || (peek() == '.' && isAlNum(peek(1)))) advance();

        CharBuffer buf = CharBuffer.wrap(str, start, pos).slice();
        return new Token(TokenType.LINK, line, column, buf);
    }

    private Token scanString() {
        advance();
        final int start = pos;

        while (isNext()) ;

        if (peek() != '"')
            throw new RuntimeException("Unterminated string literal at line " + line + ", column " + column);
        advance();

        CharBuffer buf = CharBuffer.wrap(str, start, pos - 1).slice();
        return new Token(TokenType.STRING, line, column, buf);
    }

    private Token scanSingleToken(TokenType type) {
        final int start = pos;
        advance();

        CharBuffer buf = CharBuffer.wrap(str, start, pos).slice();
        return new Token(type, line, column, buf);
    }

    private Token scanNumber() {
        final int start = pos;

        if (peek() == '+' || peek() == '-') {
            advance();
        }

        if (peek() == '0') {
            char prefix = peek(1);

            if (prefix == 'x' || prefix == 'X') {
                advance();
                do {
                    advance();
                } while (isHexDigit(peek()));
                return makeNumberToken(start);
            }

            if (prefix == 'b' || prefix == 'B') {
                advance();
                advance();
                do {
                    advance();
                } while (peek() == '0' || peek() == '1');

                return makeNumberToken(start);
            }

            if (prefix == 'o' || prefix == 'O') {
                advance();
                do {
                    advance();
                } while (isOctalDigit(peek()));

                return makeNumberToken(start);
            }
        }

        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.') {
            do {
                advance();
            } while (isDigit(peek()));
        }

        if (peek() == 'e' || peek() == 'E') {
            advance();
            if (peek() == '+' || peek() == '-') {
                advance();
            }

            do {
                advance();
            } while (isDigit(peek()));
        }

        return makeNumberToken(start);
    }

    private Token scanRef() {
        advance();
        final int start = pos;

        while (isAlNum(peek())) advance();

        CharBuffer buf = CharBuffer.wrap(str, start, pos).slice();
        return new Token(TokenType.REF, line, column, buf);
    }

    public List<Token> tokenize(String input) {
        str = input;
        pos = 0;
        line = 1;
        column = 1;

        ArrayList<Token> tokens = new ArrayList<>();

        while (peek() != '\0') {
            char c = peek();

            if (isAlpha(c)) tokens.add(scanIdentifier());
            else if (c == '"') tokens.add(scanString());
            else if (isDigit(c) || c == '-') tokens.add(scanNumber());
            else if (c == '=') tokens.add(scanSingleToken(TokenType.EQUALS));
            else if (c == '{') tokens.add(scanSingleToken(TokenType.LBRACE));
            else if (c == '}') tokens.add(scanSingleToken(TokenType.RBRACE));
            else if (c == '[') tokens.add(scanSingleToken(TokenType.LBRACKET));
            else if (c == ']') tokens.add(scanSingleToken(TokenType.RBRACKET));
            else if (c == '@') tokens.add(scanRef());
            else if (c == '$') tokens.add(scanLink());
            else if (c == '!') tokens.add(scanSingleToken(TokenType.BANG));
            else {
                final int before = pos;
                skipIgnored();

                if (pos == before) {
                    throw new RuntimeException("Unexpected character: " + c + " at line " + line + ", column " + column);
                }
            }
        }
        CharBuffer buf = CharBuffer.wrap(str, pos, pos).slice();
        if (peek() == '\0') {
            tokens.add(new Token(TokenType.EOF, line, column, buf));
        }

        return tokens;
    }
}
