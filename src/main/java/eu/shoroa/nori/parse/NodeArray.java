package eu.shoroa.nori.parse;

import java.util.List;

public class NodeArray {
    public final List<Node<?>> nodes;
    public boolean[] valueIsLinks;
    public Node<?> defaultValue;
    public boolean defaultValueIsLink;

    public NodeArray(List<Node<?>> nodes, Node<?> defaultValue) {
        this.nodes = nodes;
        this.valueIsLinks = null;
        this.defaultValue = defaultValue;
        this.defaultValueIsLink = false;
    }
}
