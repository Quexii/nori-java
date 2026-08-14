# Nori

> Suspiciously JSON shaped hobby data format

Nori is a tiny data format made for personal projects. Basically JSON, but with a few extra features bolted on.

## Example

```nori
character {
    name = "Steve"
    rarity = 4
    bool = false

    capabilities { // object block
        health = 100
        attack = 20
        cooldown = 1
    }

    items = [ /* array of any-type elements */
        {
            name = "Sword"
            damage = 25
            durability = 100
            sprite = @swordSprite
        }
        {
            name = "Pickaxe"
            damage = 4
            durability = 100
            sprite = @pickaxeSprite
        }
        ! = $items.0 // default value linking to first array element
    ]
}
```

## Basic usage

```java
Nori nori = new Nori();
nori.addReference("swordSprite", "sword.png");
nori.addReference("pickaxeSprite", "pickaxe.png");

String input = ...;

Parser parser = nori.parser(input);
Node<?> root = parser.parse();
Nori.printNode(root, 0); // print the node tree

String name = root.get("name").getString().toString();
int rarity = (int) root.get("rarity").getNumber();

int itemCount = root.get("items").count();
for (int i = 0; i < itemCount; i++) {
    Node<?> item = root.get("items." + i);
    String itemName = item.get("name").getString().toString();
    String sprite = item.get("sprite").getRef(null).toString();
}
```

## Status

Hobby project, API and syntax may change without warning.