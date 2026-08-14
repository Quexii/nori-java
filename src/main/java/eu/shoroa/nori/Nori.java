package eu.shoroa.nori;

import eu.shoroa.nori.parse.Node;
import eu.shoroa.nori.parse.Parser;
import eu.shoroa.nori.parse.Property;
import eu.shoroa.nori.token.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public class Nori {
    private final Map<String, Object> refMap = new HashMap<>();
    private final Tokenizer tokenizer = new Tokenizer();

    public <T> void addReference(String key, T value) {
        refMap.put(key, value);
    }

    public Parser parser(String input) {
        if (!input.endsWith("\0")) {
            input += "\0";
        }

        return new Parser(tokenizer.tokenize(input), refMap);
    }

    private static void printIndent(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
    }

    private static void printObject(Node.Obj node, int depth) {
        System.out.print("Object");
        if (node.value.name.len > 0) {
            System.out.print(" " + node.value.name.buffer.toString());
        }
        System.out.print("\n");

        for (Property property : node.value.properties) {
            printIndent(depth + 1);
            System.out.printf("Property: %s\n", property.token.buffer.toString());
            printNode(property.value, depth + 2);
        }

        printIndent(depth);
    }

    private static void printArray(Node.Array node, int depth) {
        System.out.print("Array\n");

        for (Node<?> node1 : node.value.nodes) {
            printNode(node1, depth + 1);
        }
    }

    public static void printNode(Node<?> node, int depth) {
        if (node == null) return;

        printIndent(depth);
        switch (node.type) {
            case NUMBER:
                System.out.printf("Number: %f\n", (double) node.value);
                break;
            case BOOL:
                System.out.printf("Bool: %b\n", node.value);
                break;
            case STRING:
                System.out.printf("String: %s\n", ((Node.String) node).value.buffer.toString());
                break;
            case IDENTIFIER:
                System.out.printf("Identifier: %s\n", ((Node.Identifier) node).value.buffer.toString());
                break;
            case REF:
                System.out.printf("Reference: %s\n", node.value);
                break;
            case LINK:
                System.out.printf("Link: %s\n", ((Node.Link) node).value.buffer.toString());
                break;
            case OBJECT:
                printObject((Node.Obj) node, depth);
                break;
            case ARRAY:
                printArray((Node.Array) node, depth);
                break;
            default:
                System.out.printf("Unknown node type: %s\n", node.type.name());
        }
    }
}
