package eu.shoroa.nori.parse;

import java.util.List;

public class NodeArray {
    public final List<Node<?>> nodes;
    public boolean[] valueIsLinks;

    public NodeArray(List<Node<?>> nodes) {
        this.nodes = nodes;
        this.valueIsLinks = null;
    }
}
