import eu.shoroa.nori.Nori;
import eu.shoroa.nori.parse.Node;
import eu.shoroa.nori.parse.Parser;

import java.io.BufferedInputStream;
import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Nori nori = new Nori();
        nori.addReference("swordSprite", "sword.png");
        nori.addReference("pickaxeSprite", "pickaxe.png");

        BufferedInputStream in = (BufferedInputStream) ParserTest.class.getClassLoader().getResourceAsStream("test.nori");
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = in.read()) != -1 && b != 0) {
            sb.append((char) b);
        }
        String input = sb.toString();

        Parser parser = nori.parser(input);
        Node<?> root = parser.parse();
        Nori.printNode(root, 0);

        System.out.println();
        System.out.println();

        Character.Item[] items = new Character.Item[root.get("items").count()];
        for (int i = 0; i < root.get("items").count(); i++) {
            if (root.get("items." + i) instanceof Node.Obj) {
                items[i] = new Character.Item(
                        root.get("items." + i + ".name").getString().toString(),
                        (int) root.get("items." + i + ".damage").getNumber(),
                        (int) root.get("items." + i + ".durability").getNumber(),
                        root.get("items." + i + ".sprite").getRef(null).toString()
                );
            }
        }

        Character character = new Character(
                root.get("name").getString().toString(),
                (int) root.get("rarity").getNumber(),
                new Character.Capabilities(
                        (int) root.get("capabilities.health").getNumber(),
                        (int) root.get("capabilities.attack").getNumber(),
                        (int) root.get("capabilities.cooldown").getNumber()
                ),
                items
        );

        System.out.println("Character:");
        System.out.println("  Name: " + character.name);
        System.out.println("  Rarity: " + character.rarity);
        System.out.println("  Capabilities:");
        System.out.println("    Health: " + character.capabilities.health);
        System.out.println("    Attack: " + character.capabilities.attack);
        System.out.println("    Cooldown: " + character.capabilities.cooldown);
        System.out.println("  Items:");
        for (Character.Item item : character.items) {
            if (item == null) continue;
            System.out.println("    Item:");
            System.out.println("      Name: " + item.name);
            System.out.println("      Damage: " + item.damage);
            System.out.println("      Durability: " + item.durability);
            System.out.println("      Sprite: " + item.sprite);
        }
    }

    static class Character {
        public final String name;
        public final int rarity;
        public final Capabilities capabilities;
        public final Item[] items;

        Character(String name, int rarity, Capabilities capabilities, Item[] items) {
            this.name = name;
            this.rarity = rarity;
            this.capabilities = capabilities;
            this.items = items;
        }

        static class Capabilities {
            public final int health, attack, cooldown;

            Capabilities(int health, int attack, int cooldown) {
                this.health = health;
                this.attack = attack;
                this.cooldown = cooldown;
            }
        }

        static class Item {
            public final String name;
            public final int damage, durability;
            public final String sprite;

            Item(String name, int damage, int durability, String sprite) {
                this.name = name;
                this.damage = damage;
                this.durability = durability;
                this.sprite = sprite;
            }
        }
    }
}
