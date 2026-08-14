package eu.shoroa.nori.parse;

import eu.shoroa.nori.token.Token;

public class Property {
    public final Token token;
    public Node<?> value;
    public boolean valueIsLink;

    public Property(Token token, Node<?> value, boolean valueIsLink) {
        this.token = token;
        this.value = value;
        this.valueIsLink = valueIsLink;
    }
}
