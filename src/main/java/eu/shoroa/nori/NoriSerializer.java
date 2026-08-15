package eu.shoroa.nori;

import eu.shoroa.nori.parse.Node;
import eu.shoroa.nori.parse.Property;

public final class NoriSerializer {

    private NoriSerializer() {
    }

    public static String stringify(Node<?> node) {
        StringBuilder out = new StringBuilder();
        stringifyNode(node, out, 0);
        return out.toString();
    }

    private static void stringifyNode(Node<?> node, StringBuilder out, int depth) {

        if (node == null) {
            return;
        }

        switch (node.type) {
            case NUMBER:
                stringifyNumber((Node.Number) node, out);
                break;

            case BOOL:
                out.append(node.value);
                break;

            case STRING:
                appendString(out, ((Node.String) node).value.buffer);
                break;

            case IDENTIFIER:
                out.append(((Node.Identifier) node).value.buffer);
                break;

            case REF:
                out.append('@').append(node.value);
                break;

            case LINK:
                out.append('$').append(((Node.Link) node).value.buffer);
                break;

            case OBJECT:
                stringifyObject((Node.Obj) node, out, depth);
                break;

            case ARRAY:
                stringifyArray((Node.Array) node, out, depth);
                break;

            default:
                throw new IllegalArgumentException("Cannot stringify node type: " + node.type);
        }
    }

    private static void stringifyNumber(Node.Number node, StringBuilder out) {
        switch (node.value.type) {
            case DOUBLE:
            case FLOAT:
                out.append(node.value.doubleValue);
                break;
            case LONG:
                out.append(node.value.longValue);
                break;
            case INT:
                out.append(node.value.intValue);
                break;
            case BINARY:
                out.append("0b").append(Integer.toBinaryString(node.value.intValue));
                break;
            case OCTAL:
                out.append("0o").append(Integer.toOctalString(node.value.intValue));
                break;
            case HEX:
                out.append("0x").append(Integer.toHexString(node.value.intValue));
                break;
            default:
                throw new IllegalArgumentException("Cannot stringify number type: " + node.value.type);
        }
    }

    private static void stringifyObject(Node.Obj node, StringBuilder out, int depth) {

        CharSequence name = node.value.name.buffer;

        if (name != null && name.length() > 0) {
            out.append(name).append(' ');
        }

        out.append("{\n");

        for (Property property : node.value.properties) {
            indent(out, depth + 1);

            if (property.token != null && property.token.buffer != null) {
                out.append(property.token.buffer).append(" = ");
            }

            stringifyNode(property.value, out, depth + 1);
            out.append('\n');
        }

        indent(out, depth);
        out.append('}');
    }

    private static void stringifyArray(Node.Array node, StringBuilder out, int depth) {

        out.append("[\n");

        for (Node<?> child : node.value.nodes) {
            indent(out, depth + 1);
            stringifyNode(child, out, depth + 1);
            out.append('\n');
        }

        indent(out, depth);
        out.append(']');
    }

    private static void appendString(StringBuilder out, CharSequence value) {

        out.append('"');

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            switch (c) {
                case '\\':
                    out.append("\\\\");
                    break;
                case '"':
                    out.append("\\\"");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                default:
                    out.append(c);
            }
        }

        out.append('"');
    }

    private static void indent(StringBuilder out, int depth) {
        for (int i = 0; i < depth; i++) {
            out.append("    ");
        }
    }
}