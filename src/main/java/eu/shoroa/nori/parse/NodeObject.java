package eu.shoroa.nori.parse;

import eu.shoroa.nori.token.Token;

import java.util.List;

public class NodeObject {
    public final Token name;
    public final List<Property> properties;
    public Node<?> defaultValue;
    public boolean defaultValueIsLink;

    public NodeObject(Token name, List<Property> properties, Node<?> defaultValue, boolean defaultValueIsLink) {
        this.name = name;
        this.properties = properties;
        this.defaultValue = defaultValue;
        this.defaultValueIsLink = defaultValueIsLink;
    }
}
