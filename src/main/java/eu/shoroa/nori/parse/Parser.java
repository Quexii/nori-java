package eu.shoroa.nori.parse;

import eu.shoroa.nori.token.Token;
import eu.shoroa.nori.token.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {
    private final List<Token> tokens;
    private final Map<String, Object> refMap;
    private int pos;

    public Parser(List<Token> tokens, Map<String, Object> refMap) {
        this.tokens = tokens;
        this.refMap = refMap;
        this.pos = 0;
    }

    private Token peek(int offset) {
        if (pos + offset < 0 || pos + offset >= tokens.size()) {
            throw new IndexOutOfBoundsException("Offset [" + offset + "] is out of bounds for the tokens array of length [" + tokens.size() + "] for index [" + (pos + offset) + "].");
        }
        return tokens.get(pos + offset);
    }

    private Token peek() {
        return peek(0);
    }

    private Token advance() {
        if (pos < tokens.size()) {
            pos++;
        }

        return peek(-1);
    }

    private Token expect(TokenType type) {
        if (peek().type != type) {
            throw new IllegalStateException("Expected token of type [" + type + "] but found [" + peek().type + "] at line [" + peek().line + "], column [" + peek().column + "].");
        }

        return advance();
    }

    private Property parseProperty() {
        Token name = expect(TokenType.IDENTIFIER);

        if (peek().type == TokenType.LBRACE) {
            Node<NodeObject> value = parseObjectBody(name);
            return new Property(name, value, false);
        }

        expect(TokenType.EQUALS);
        Node<?> value = parseValue();

        return new Property(name, value, false);
    }

    private Node.Number parseNumber() {
        final Token tok = expect(TokenType.NUMBER);

        return new Node.Number(true, Double.parseDouble(tok.buffer.toString()));
    }

    private Node.String parseString() {
        final Token tok = expect(TokenType.STRING);

        return new Node.String(true, tok);
    }

    private Node.Bool parseBool() {
        final Token tok = expect(TokenType.BOOL);
        return new Node.Bool(true, tok.buffer.toString().equals("true"));
    }

    private Node.Obj parseObjectBody(Token name) {
        expect(TokenType.LBRACE);

        Node<?> defaultValue = null;
        List<Property> properties = new ArrayList<>();

        while (peek().type != TokenType.RBRACE) {
            if (peek().type == TokenType.BANG) {
                if (defaultValue != null) {
                    throw new IllegalStateException("Duplicate \"!\" default value at line " + peek().line + ", column " + peek().column);
                }

                advance();
                expect(TokenType.EQUALS);
                defaultValue = parseValue();
                continue;
            }

            properties.add(parseProperty());
        }

        expect(TokenType.RBRACE);

        return new Node.Obj(NodeType.OBJECT, true, new NodeObject(name, properties, defaultValue, false));
    }

    private Node.Obj parseObject() {
        Token name;
        if (peek().type == TokenType.IDENTIFIER)
            name = expect(TokenType.IDENTIFIER);
        else
            name = new Token(TokenType.IDENTIFIER, peek().start, 0, peek().line, peek().column, null);

        return parseObjectBody(name);
    }

    private Node.Identifier parseIdentifier() {
        final Token tok = expect(TokenType.IDENTIFIER);

        return new Node.Identifier(true, tok);
    }

    private Node.Reference parseReference() {
        final Token tok = expect(TokenType.REF);

        Object value = refMap.get(tok.buffer.toString());
        if (value == null) {
            throw new IllegalStateException("Unresolved reference @" + tok.buffer + " at line " + tok.line + ", col " + tok.column + " (not registered via Nori#addReference before parsing)");
        }

        return new Node.Reference(true, value);
    }

    private Node.Array parseArray() {
        expect(TokenType.LBRACKET);

        List<Node<?>> elements = new ArrayList<>();

        while (peek().type != TokenType.RBRACKET) {
            Node<?> node = parseValue();

            if (node != null) {
                elements.add(node);
            }
        }

        expect(TokenType.RBRACKET);

        return new Node.Array(NodeType.ARRAY, true, new NodeArray(elements));
    }

    private Node.Link parseLink() {
        final Token tok = expect(TokenType.LINK);

        return new Node.Link(true, tok);
    }

    private Node<?> parseValue() {
        switch (peek().type) {
            case NUMBER:
                return parseNumber();
            case STRING:
                return parseString();
            case BOOL:
                return parseBool();
            case IDENTIFIER:
                if (peek(1).type == TokenType.LBRACE)
                    return parseObject();
                return parseIdentifier();
            case REF:
                return parseReference();
            case LBRACKET:
                return parseArray();
            case LBRACE:
                return parseObject();
            case LINK:
                return parseLink();
            default:
                Token tok = peek();
                advance();

                System.err.println(
                        "Unexpected token [" + tok.type +
                                "] line [" + tok.line +
                                "], column [" + tok.column + "]"
                );

                return null;
        }
    }

    private static Node<?> resolveLink(Node<?> root, Node<?> linkNode) {
        Node<?> target = root.get(((Node.Link) linkNode).value.buffer.toString());

        if (target == null) {
            throw new IllegalStateException("Unresolved link " + ((Node.Link) linkNode).value.buffer + " at line " + ((Node.Link) linkNode).value.line + ", col " + ((Node.Link) linkNode).value.column);
        }
        return target;
    }

    private static void resolveLinksIn(Node<?> root, Node<?> node) {
        if (node == null) return;

        switch (node.type) {
            case OBJECT:
                Node.Obj obj = (Node.Obj) node;
                for (Property property : obj.value.properties) {
                    if (property.value.type == NodeType.LINK) {
                        property.value = resolveLink(root, property.value);
                        property.valueIsLink = true;
                    } else {
                        resolveLinksIn(root, property.value);
                    }
                }

                if (obj.value.defaultValue != null && obj.value.defaultValue.type == NodeType.LINK) {
                    obj.value.defaultValue = resolveLink(root, obj.value.defaultValue);
                    obj.value.defaultValueIsLink = true;
                } else {
                    resolveLinksIn(root, obj.value.defaultValue);
                }
                break;
            case ARRAY:
                Node.Array array = (Node.Array) node;
                int i = 0;
                for (Node<?> slot : array.value.nodes) {
                    if (slot.type == NodeType.LINK) {
                        Node<?> resolved = resolveLink(root, slot);

                        array.value.nodes.set(i, resolved);

                        if (array.value.valueIsLinks == null) {
                            array.value.valueIsLinks = new boolean[array.value.nodes.size()];
                        }

                        array.value.valueIsLinks[i] = true;
                    } else {
                        resolveLinksIn(root, slot);
                    }
                    i++;
                }
                break;
            default:
                break;
        }
    }

    public Node<?> parse() {
        ArrayList<Node<?>> elements = new ArrayList<>();

        while (peek().type != TokenType.EOF) {
            Node<?> node = parseValue();
            if (node != null) {
                elements.add(node);
            }
        }

        Node.Array root = new Node.Array(NodeType.ARRAY, true, new NodeArray(elements));

        resolveLinksIn(root, root);

        return root;
    }
}
