package eu.shoroa.nori;

import eu.shoroa.nori.parse.*;
import eu.shoroa.nori.token.Token;
import eu.shoroa.nori.token.TokenType;
import eu.shoroa.nori.token.Tokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Nori {
    private final Map<String, Object> refMap = new HashMap<>();
    private final Tokenizer tokenizer = new Tokenizer();

    public <T> void addReference(String key, T value) {
        refMap.put(key, value);
    }

    public Node<?> parse(String input) {
        if (!input.endsWith("\0")) {
            input += "\0";
        }

        return new Parser(tokenizer.tokenize(input), refMap).parse();
    }

    private static Token makeOwnedToken(@Nullable final CharSequence name) {
        return new Token(TokenType.IDENTIFIER, 0, 0, name == null ? null : CharBuffer.wrap(name));
    }

    public Node.Number newNumber(@NotNull Number number) {
        return new Node.Number(number.doubleValue());
    }

    public Node.String newString(@NotNull final CharSequence str) {
        return new Node.String(new Token(TokenType.STRING, 0, 0, CharBuffer.wrap(str)));
    }

    public Node.Bool newBool(@NotNull Boolean state) {
        return new Node.Bool(state);
    }

    public Node.Obj newObject(@Nullable final CharSequence name) {
        return new Node.Obj(new NodeObject(makeOwnedToken(name), new ArrayList<>(), null, false));
    }

    public Node.Array newArray() {
        return new Node.Array(new NodeArray(new ArrayList<>(), null));
    }

    public Node.Reference newRef(final Object object) {
        return new Node.Reference(object);
    }

    public Node.Link newLink(final CharSequence path) {
        return new Node.Link(new Token(TokenType.LINK, 0, 0, CharBuffer.wrap(path)));
    }

    public void insertProperty(Node<?> root, CharSequence name, Node<?> value) {
        if (root instanceof Node.Obj) {
            ((Node.Obj) root).value.properties.add(new Property(makeOwnedToken(name), value, false));
        } else if (root instanceof Node.Array) {
            ((Node.Array) root).value.nodes.add(value);
        } else {
            throw new IllegalArgumentException("Root node must be of type Node.Obj or Node.Array!");
        }
    }

    public void setDefault(Node<?> root, Node<?> defaultValue) {
        if (root instanceof Node.Obj) {
            ((Node.Obj) root).value.defaultValue = defaultValue;
            if (defaultValue.type == NodeType.LINK)
                ((Node.Obj) root).value.defaultValueIsLink = true;
        } else if (root instanceof Node.Array) {
            ((Node.Array) root).value.defaultValue = defaultValue;
            if (defaultValue.type == NodeType.LINK)
                ((Node.Array) root).value.defaultValueIsLink = true;
        } else {
            throw new IllegalArgumentException("Root node must be of type Node.Obj or Node.Array!");
        }
    }
}
