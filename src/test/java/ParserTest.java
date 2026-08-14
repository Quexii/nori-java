import eu.shoroa.nori.Nori;
import eu.shoroa.nori.parse.Node;
import eu.shoroa.nori.parse.Parser;

import java.io.BufferedInputStream;
import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Nori nori = new Nori();
        nori.addReference("amiya", "value1");

        BufferedInputStream in = (BufferedInputStream) ParserTest.class.getClassLoader().getResourceAsStream("test.nori");
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = in.read()) != -1 && b != 0) {
            sb.append((char) b);
        }
        String input = sb.toString();

        Parser parser = nori.parser(input);
        Node<?> root = parser.parse();

        CharSequence name = root.get("name").getString();
        Node.Obj nodeBase = (Node.Obj) root.get("base");

        System.out.println(name);
        System.out.println("health = " + nodeBase.get("health").getNumber());
        System.out.println("attack = " + nodeBase.get("attack").getNumber());
        System.out.println("cooldown = " + nodeBase.get("cooldown").getNumber());
        System.out.println();

        System.out.println(root.get("art").getDefault().getRef(null));
        System.out.println("rarity = " + root.get("rL").getNumber());
    }
}
