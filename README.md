# Chipped Plus

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.18.2-brightgreen?logo=minecraft&logoColor=white)
![Build Status](https://img.shields.io/badge/Build-Gradle-blue?logo=gradle&logoColor=white)
![License](https://img.shields.io/badge/License-EPL--2.0-orange.svg)

> **Expand your building capabilities with missing block variants for Chipped!**

**Chipped Plus** is an unofficial add-on for the popular **Chipped Mod**. It bridges the gap for builders by introducing essential structural variants, such as **walls, slabs, and stairs** for block textures that were previously missing them.

This repository serves as a dedicated backport and maintenance project to retain full playability and accessibility for **Minecraft 1.18.2**.

---

## ✨ Features

- 🧱 **Complete Variant Coverage:** Adds matching walls, slabs, and stairs for Chipped block designs.
- 🎨 **Seamless Integration:** Fully compatible with base Chipped palettes and recipes.
- 🔄 **1.18.2 Backport:** Specifically tuned and maintained for Minecraft 1.18.2 modpacks.

---

## 🛠️ Developer Setup & Compilation

Follow these steps to set up the development environment and compile the project from source:

### Prerequisites
* **Java Development Kit (JDK):** Version 17
* **Gradle Wrapper:** Included in repository

### Building the Project

1. **Generate Assets & Data:**
   Always execute the DataGen pipeline first. This ensures all required block models, recipes, and blockstates are properly compiled.
   ```bash
   ./gradlew runData
   ```

2. **Compile the Mod JAR:**
   Once DataGen completes, run the standard build process:
   ```bash
   ./gradlew build
   ```

3. **Locate the Output:**
   The compiled JAR file will be available in the output directory:
   ```text
   build/libs/ChippedPlus-1.18.2-*.jar
   ```

---

## 📜 License & Acknowledgments

This project is an unofficial backport maintained under open-source licenses.

* **License:** Distributed under the [Eclipse Public License - v 2.0](LICENSE).
* **Source Repository:** Hosted on [GitHub](https://github.com/xalbino/ChippedPlus/).
* **Original Author:** [puredoom](https://github.com/puredoom) — Creator of the original *Chipped Extras* mod.
* **Base Mod Credits:** [Chipped](https://www.curseforge.com/minecraft/mc-mods/chipped) created by the **Terrarium Team**. All referenced textures and core assets remain the property of their respective creators under the *Terrarium License v1*.

---

<p align="center">
  <i>Not an official Minecraft product. Not approved by or associated with Mojang or Microsoft.</i>
</p>
