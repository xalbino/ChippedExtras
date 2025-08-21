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


public class VariantLootProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private final PackOutput output;
    private final ChippedDiscoveryProvider discover;

    public VariantLootProvider(PackOutput out, ChippedDiscoveryProvider d) {
        this.output = out;
        this.discover = d;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (var e : discover.discovered()) {
            futures.add(saveBlockLoot(cache, e.stairs(), dropSelf(e.stairs())));
            futures.add(saveBlockLoot(cache, e.wall(),   dropSelf(e.wall())));

            futures.add(saveBlockLoot(cache, e.slab(),   slabDrops(e.slab())));
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override public String getName() { return "ChippedExttras Loot (raw JSON)"; }

    private CompletableFuture<?> saveBlockLoot(CachedOutput cache, String blockRL, Map<String,Object> json) {
        String path = ResourceLocation.tryParse(blockRL).getPath();
        Path outPath = output.getOutputFolder().resolve("data/" + Chippedexttras.MODID + "/loot_tables/blocks/" + path + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), outPath);
    }

    private static Map<String,Object> dropSelf(String blockRL) {
        return map(
                "type","minecraft:block",
                "pools", List.of(map(
                        "rolls",1,
                        "entries", List.of(map(
                                "type","minecraft:item",
                                "name", blockRL
                        )),
                        "conditions", List.of(map("condition","minecraft:survives_explosion"))
                ))
        );
    }

    private static Map<String,Object> slabDrops(String blockRL) {
        return map(
                "type","minecraft:block",
                "pools", List.of(map(
                        "rolls",1,
                        "entries", List.of(map(
                                "type","minecraft:alternatives",
                                "children", List.of(
                                        map(
                                                "type","minecraft:item",
                                                "name", blockRL,
                                                "conditions", List.of(map(
                                                        "condition","minecraft:block_state_property",
                                                        "block", blockRL,
                                                        "properties", map("type","double")
                                                )),
                                                "functions", List.of(map(
                                                        "function","minecraft:set_count",
                                                        "count", 2
                                                ))
                                        ),
                                        map(
                                                "type","minecraft:item",
                                                "name", blockRL
                                        )
                                )
                        )),
                        "conditions", List.of(map("condition","minecraft:survives_explosion"))
                ))
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
