package net.xalbino.chippedextras.datagen;

import net.xalbino.chippedextras.ChippedExtras;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.LanguageProvider;

public class VariantLang extends LanguageProvider {
    private final ChippedDiscoveryProvider discover;

    public VariantLang(DataGenerator gen, ChippedDiscoveryProvider discover) {
        super(gen, ChippedExtras.MODID, "en_us");
        this.discover = discover;
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ChippedExtras.MODID, "Chipped Extras");

        for (var e : discover.discovered()) {
            if (e == null) continue;

            if (e.slab() != null) {
                String p = path(e.slab());
                if (p != null) {
                    add("block." + ChippedExtras.MODID + "." + p, formatName(p));
                }
            }

            if (e.stairs() != null) {
                String p = path(e.stairs());
                if (p != null) {
                    add("block." + ChippedExtras.MODID + "." + p, formatName(p));
                }
            }

            if (e.wall() != null) {
                String p = path(e.wall());
                if (p != null) {
                    add("block." + ChippedExtras.MODID + "." + p, formatName(p));
                }
            }
        }
    }

    private static String path(String rl) {
        if (rl == null) return null;
        ResourceLocation loc = ResourceLocation.tryParse(rl);
        return loc != null ? loc.getPath() : null;
    }

    private static String formatName(String path) {
        // Gets rid of _ in names
        String[] words = path.split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                        .append(w.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}