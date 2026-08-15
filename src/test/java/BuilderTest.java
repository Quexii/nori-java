import eu.shoroa.nori.Nori;
import eu.shoroa.nori.NoriSerializer;
import eu.shoroa.nori.parse.Node;

public class BuilderTest {
    public static void main(String[] args) {
        Nori n = new Nori();
        n.addReference("swordSprite", "sword.png");

        Node.Obj root = n.newObject(null);
        n.insertProperty(root, "name", n.newString("Sword"));
        n.insertProperty(root, "damage", n.newDouble(10));
        n.insertProperty(root, "sprite", n.newRef("swordSprite"));
        n.insertProperty(root, "link", n.newLink("name"));
        n.insertProperty(root, "array", n.newArray());
        n.insertProperty(root, null, n.newObject(null));
        n.setDefault(root, root.get("name"));
        for (int i = 0; i < 5; i++) {
            Node.Obj obj = n.newObject(null);
            n.insertProperty(obj, "index", n.newString("" + i));
            n.insertProperty(root.get("array"), null, obj);
        }

        System.out.println(NoriSerializer.stringify(root));
    }
}
