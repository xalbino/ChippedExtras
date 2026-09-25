# ChippedExtras

Welcome to the **ChippedExtras** developer repository!

This project seeks to retain playability and access to ChippedExtras for common modded versions, starting with Minecraft 1.18.2.

---

## Compilation & Setup

To compile the workspace, always run the DataGen pipeline first to generate all necessary block models, recipes, and blockstates:

```bash
./gradlew runData
```

After the DataGen process completes, you can build the Mod JAR:
```bash
./gradlew build
```

## License & Credits

This mod is an unofficial 1.18.2 backport of **Chipped Extras**.

* **License:** Distributed under the [Eclipse Public License - v 2.0](LICENSE).
* **Source Code:** Available on [GitHub](https://github.com/xalbino/ChippedExtras/).
* **Original Author:** [puredoom](https://github.com/puredoom) (Original creator of Chipped Extras).
* **Base Mod:** [Chipped](https://www.curseforge.com/minecraft/mc-mods/chipped) by the **Terrarium Team**. All referenced textures and base assets belong to their respective creators under the Terrarium License v1.
