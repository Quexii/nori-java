package eu.shoroa.nori;

import eu.shoroa.nori.parse.Node;
import eu.shoroa.nori.parse.Property;

public final class NodePrinter {

    private NodePrinter() {
    }

    public static void print(Node<?> node) {
        print(node, 0);
    }

    public static void print(Node<?> node, int depth) {
        if (node == null) {
            return;
        }

        printIndent(depth);

        switch (node.type) {
            case NUMBER:
                System.out.printf("Number %s%n", node.value);
                break;

            case BOOL:
                System.out.printf("Boolean %s%n", node.value);
                break;

            case STRING:
                System.out.printf("String \"%s\"%n", ((Node.String) node).value.buffer);
                break;

            case IDENTIFIER:
                System.out.printf("Identifier %s%n", ((Node.Identifier) node).value.buffer);
                break;

            case REF:
                System.out.printf("Reference %s%n", node.value);
                break;

            case LINK:
                System.out.printf("Link %s%n", ((Node.Link) node).value.buffer);
                break;

            case OBJECT:
                printObject((Node.Obj) node, depth);
                break;

            case ARRAY:
                printArray((Node.Array) node, depth);
                break;

            default:
                System.out.printf("Unknown %s%n", node.type);
        }
    }

    private static void printObject(Node.Obj node, int depth) {
        CharSequence name = node.value.name.buffer;

        System.out.printf("Object%s%n", name == null || name.length() == 0 ? "" : " " + name);

        for (Property property : node.value.properties) {
            printIndent(depth + 1);
            System.out.printf("Property %s%n", property.token.buffer);
            print(property.value, depth + 2);
        }
    }

    private static void printArray(Node.Array node, int depth) {
        System.out.println("Array");

        for (Node<?> child : node.value.nodes) {
            print(child, depth + 1);
        }
    }

    private static void printIndent(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
    }
}