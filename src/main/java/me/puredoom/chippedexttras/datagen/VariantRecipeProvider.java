package me.puredoom.chippedexttras.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.puredoom.chippedexttras.Chippedexttras;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class VariantRecipeProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private final PackOutput output;
    private final ChippedDiscoveryProvider discover;

    public VariantRecipeProvider(PackOutput out, ChippedDiscoveryProvider d) {
        this.output = out;
        this.discover = d;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (var e : discover.discovered()) {
            ResourceLocation base   = ResourceLocation.tryParse(e.base());
            ResourceLocation slab   = ResourceLocation.tryParse(e.slab());
            ResourceLocation stairs = ResourceLocation.tryParse(e.stairs());
            ResourceLocation wall   = ResourceLocation.tryParse(e.wall());

            // Stonecutting (1->1; slab 1->2)
            if (stairs != null) futures.add(saveRecipe(cache, "stonecutting/" + stairs.getPath(), stonecut(base, stairs, 1)));
            if (wall   != null) futures.add(saveRecipe(cache, "stonecutting/" + wall.getPath(),   stonecut(base, wall,   1)));
            if (slab   != null) futures.add(saveRecipe(cache, "stonecutting/" + slab.getPath(),   stonecut(base, slab,   2)));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override public String getName() { return "ChippedExttras Recipes (stonecut-only)"; }

    private CompletableFuture<?> saveRecipe(CachedOutput cache, String rel, Map<String,Object> json) {
        Path p = output.getOutputFolder().resolve("data/" + Chippedexttras.MODID + "/recipes/" + rel + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), p);
    }

    private static Map<String, Object> stonecut(ResourceLocation ingredientItem, ResourceLocation resultItem, int count) {
        return map(
                "type", "minecraft:stonecutting",
                "ingredient", map("item", ingredientItem.toString()),
                "result", resultItem.toString(),
                "count", count
        );
    }

    @SafeVarargs
    private static <K,V> Map<K,V> map(Object... kv) {
        Map<K,V> m = new LinkedHashMap<>();
        for (int i=0;i<kv.length;i+=2) {
            @SuppressWarnings("unchecked") K k = (K) kv[i];
            @SuppressWarnings("unchecked") V v = (V) kv[i+1];
            m.put(k,v);
        }
        return m;
    }
}
