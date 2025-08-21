package me.puredoom.chippedexttras.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class ChippedDiscoveryProvider implements DataProvider {
    private final PackOutput output;
    private final List<Entry> entries = new ArrayList<>();


    private static final Set<String> KEEP_EXACT = Set.of("brick", "bricks", "tile", "tiles", "stone", "stones");

    private static final Set<String> KEEP_ENDS_WITH_STONE_EXCEPT = Set.of("redstone");


    private static final Set<String> DISALLOWED_TOKENS = Set.of(
            "dripstone", "dripstones",
            "bars",
            "lamp", "lamps",
            "lantern", "lanterns",
            "torch", "torches",
            "table", "tables",
            "workbench", "workbenches",
            "glassblower", "glassblowing"
    );

    public ChippedDiscoveryProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();

        ForgeRegistries.BLOCKS.getKeys().forEach(rl -> {
            if (!"chipped".equals(rl.getNamespace())) return;

            final String path = rl.getPath();


            if (path.endsWith("_slab") || path.endsWith("_stairs") || path.endsWith("_wall")) return;


            final String[] tokens = path.toLowerCase(Locale.ROOT).split("[_\\-]");


            if (containsAny(tokens, DISALLOWED_TOKENS)) return;


            if (!isBrickStoneOrTile(tokens)) return;

            final String base   = rl.toString();
            final String slab   = new ResourceLocation("chippedexttras", path + "_slab").toString();
            final String stairs = new ResourceLocation("chippedexttras", path + "_stairs").toString();
            final String wall   = new ResourceLocation("chippedexttras", path + "_wall").toString();

            entries.add(new Entry(base, slab, stairs, wall));
        });

        final Path outPath = output.getOutputFolder().resolve("assets/chippedexttras/generated/registry.json");
        final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        System.out.println("[chippedexttras] discovered " + entries.size() + " chipped base blocks (bricks/stones/tiles)");
        return DataProvider.saveStable(cache, gson.toJsonTree(entries), outPath);
    }

    public List<Entry> discovered() { return entries; }

    @Override
    public String getName() { return "ChippedExttras Discovery & Registry List (bricks/stones/tiles)"; }

    public static record Entry(String base, String slab, String stairs, String wall) {}

    private static boolean containsAny(String[] tokens, Set<String> set) {
        for (String t : tokens) if (set.contains(t)) return true;
        return false;
    }

    private static boolean isBrickStoneOrTile(String[] tokens) {
        for (String t : tokens) {
            if (KEEP_EXACT.contains(t)) return true;
            if (t.endsWith("stone") && !KEEP_ENDS_WITH_STONE_EXCEPT.contains(t)) return true; // sandstone/cobblestone/endstone...
        }
        return false;
    }
}
