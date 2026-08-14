package eu.shoroa.nori.parse;

import eu.shoroa.nori.token.Token;

@SuppressWarnings("unused")
public abstract class Node<T> {
    public final NodeType type;
    public final boolean ownsString;
    public final T value;

    protected Node(NodeType type, boolean ownsString, T value) {
        this.type = type;
        this.ownsString = ownsString;
        this.value = value;
    }

    public static class Number extends Node<Double> {
        public Number(boolean ownsString, Double value) {
            super(NodeType.NUMBER, ownsString, value);
        }
    }

    public static class String extends Node<Token> {
        public String(boolean ownsString, Token value) {
            super(NodeType.STRING, ownsString, value);
        }
    }

    public static class Identifier extends Node<Token> {
        public Identifier(boolean ownsString, Token value) {
            super(NodeType.IDENTIFIER, ownsString, value);
        }
    }

    public static class Bool extends Node<Boolean> {
        public Bool(boolean ownsString, Boolean value) {
            super(NodeType.BOOL, ownsString, value);
        }
    }

    public static class Obj extends Node<NodeObject> {
        protected Obj(NodeType type, boolean ownsString, NodeObject value) {
            super(type, ownsString, value);
        }
    }

    public static class Array extends Node<NodeArray> {
        protected Array(NodeType type, boolean ownsString, NodeArray value) {
            super(type, ownsString, value);
        }
    }

    public static class Reference extends Node<Object> {
        public Reference(boolean ownsString, Object value) {
            super(NodeType.REF, ownsString, value);
        }
    }

    public static class Link extends Node<Token> {
        public Link(boolean ownsString, Token value) {
            super(NodeType.LINK, ownsString, value);
        }
    }

    private static boolean tokenEq(final Token tok, final CharSequence comp) {
        if (tok.buffer.length() != comp.length())
            return false;

        for (int i = 0; i < comp.length(); i++) {
            if (tok.buffer.charAt(i) != comp.charAt(i))
                return false;
        }

        return true;
    }

    private static Node<?> nodeStep(Node<?> node, final CharSequence seq) {
        if (node == null || seq == null) return null;

        boolean isIndex = seq.length() > 0;
        int index = 0;
        for (int i = 0; i < seq.length(); i++) {
            if (!Character.isDigit(seq.charAt(i))) {
                isIndex = false;
                break;
            }
            index = index * 10 + (seq.charAt(i) - '0');
        }

        if (node.type == NodeType.ARRAY) {
            if (!isIndex || index >= ((Node.Array) node).value.nodes.size()) return null;
            return ((Node.Array) node).value.nodes.get(index);
        }

        if (node.type == NodeType.OBJECT) {
            for (Property prop : ((Node.Obj) node).value.properties) {
                if (tokenEq(prop.token, seq)) {
                    return prop.value;
                }
            }
        }

        return null;
    }

    public static Node<?> get(Node<?> node, final CharSequence path) {
        if (node == null || path == null)
            return null;

        Node<?> cur = node;

        if (cur.type == NodeType.ARRAY &&
                ((Node.Array) cur).value.nodes.size() == 1) {
            cur = ((Node.Array) cur).value.nodes.get(0);
        }

        int start = 0;

        while (start < path.length() && cur != null) {
            int end = start;

            while (end < path.length() && path.charAt(end) != '.')
                end++;

            cur = nodeStep(cur, path.subSequence(start, end));

            if (end >= path.length())
                break;

            start = end + 1;
        }

        return cur;
    }

    public Node<?> get(final CharSequence path) {
        return get(this, path);
    }

    public static double getNumber(Node<?> node, double fallback) {
        if (node == null || node.type != NodeType.NUMBER) return fallback;
        return ((Node.Number) node).value;
    }

    public double getNumber(double fallback) {
        return getNumber(this, fallback);
    }

    public double getNumber() {
        return getNumber(0.0);
    }

    public static boolean getBool(Node<?> node, boolean fallback) {
        if (node == null || node.type != NodeType.BOOL) return fallback;
        return ((Node.Bool) node).value;
    }

    public boolean getBool(boolean fallback) {
        return getBool(this, fallback);
    }

    public boolean getBool() {
        return getBool(false);
    }

    public static CharSequence getString(Node<?> node) {
        if (node == null) return null;

        final Token tok;
        if (node.type == NodeType.STRING)
            tok = ((Node.String) node).value;
        else if (node.type == NodeType.IDENTIFIER)
            tok = ((Node.Identifier) node).value;
        else
            return null;

        return tok.buffer;
    }

    public CharSequence getString() {
        return getString(this);
    }

    public static Object getRef(Node<?> node, Object fallback) {
        if (node == null || node.type != NodeType.REF) return fallback;
        return node.value;
    }

    public Object getRef(Object fallback) {
        return getRef(this, fallback);
    }

    public static Node<?> getDefault(Node<?> node) {
        if (node == null || node.type != NodeType.OBJECT) return null;
        return ((Node.Obj) node).value.defaultValue;
    }

    public Node<?> getDefault() {
        return getDefault(this);
    }

    public static int count(Node<?> node) {
        if (node == null) return 0;

        if (node.type == NodeType.ARRAY)
            return ((Node.Array) node).value.nodes.size();

        if (node.type == NodeType.OBJECT)
            return ((Node.Obj) node).value.properties.size();

        return 0;
    }

    public int count() {
        return count(this);
    }

    public static Node<?> getAt(Node<?> node, int index) {
        if (node == null || node.type != NodeType.ARRAY) return null;

        return ((Node.Array) node).value.nodes.get(index);
    }

    public Node<?> getAt(int index) {
        return getAt(this, index);
    }
}
