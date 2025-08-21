package me.puredoom.chippedexttras.datagen;

import me.puredoom.chippedexttras.Chippedexttras;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class VariantLang extends LanguageProvider {
    private final ChippedDiscoveryProvider d;
    public VariantLang(PackOutput out, ChippedDiscoveryProvider d) {
        super(out, Chippedexttras.MODID, "en_us");
        this.d = d;
    }
    @Override protected void addTranslations() {
        add("itemGroup." + Chippedexttras.MODID + ".chippedexttras_tab", "Chipped Extras");

        for (var e : d.discovered()) {
            add("block." + Chippedexttras.MODID + "." + path(e.stairs()), titleize(e.base()) + " Stairs");
            add("block." + Chippedexttras.MODID + "." + path(e.slab()),   titleize(e.base()) + " Slab");
            add("block." + Chippedexttras.MODID + "." + path(e.wall()),   titleize(e.base()) + " Wall");

        }
    }
    private static String path(String rl) { return net.minecraft.resources.ResourceLocation.tryParse(rl).getPath(); }
    private static String titleize(String baseRL) {
        String p = net.minecraft.resources.ResourceLocation.tryParse(baseRL).getPath().replace('_', ' ');
        String[] parts = p.split(" ");
        StringBuilder b = new StringBuilder();
        for (String s : parts) {
            if (s.isEmpty()) continue;
            b.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(' ');
        }
        return b.toString().trim();
    }
}
