package me.puredoom.chippedexttras.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.puredoom.chippedexttras.Chippedexttras;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VariantModelStateProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private final PackOutput output;
    @SuppressWarnings("unused")
    private final ExistingFileHelper efh;
    private final ChippedDiscoveryProvider discover;

    private static final Map<ResourceLocation, String> TEX_CACHE = new HashMap<>();
    private static final Map<String, JsonObject> MODEL_JSON_CACHE = new HashMap<>();
    private static final Set<String> MISSING_RES = new HashSet<>();

    public VariantModelStateProvider(PackOutput out, ExistingFileHelper efh, ChippedDiscoveryProvider d) {
        this.output = out;
        this.efh = efh;
        this.discover = d;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (var e : discover.discovered()) {
            ResourceLocation baseRL = ResourceLocation.tryParse(e.base());
            String resolvedTex = resolveBestTexture(baseRL);

            {
                String name = rlPath(e.slab());
                futures.add(saveModel(cache, name, slabModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_top", slabTopModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_double", cubeAllModel(resolvedTex)));
                futures.add(saveItemModel(cache, name, itemParent(modBlock(name))));
                futures.add(saveBlockstate(cache, name, slabBlockstate(
                        modBlock(name),
                        modBlock(name + "_top"),
                        modBlock(name + "_double")
                )));
            }

            {
                String name = rlPath(e.stairs());
                futures.add(saveModel(cache, name, stairsModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_inner", stairsInnerModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_outer", stairsOuterModel(resolvedTex)));
                futures.add(saveItemModel(cache, name, itemParent(modBlock(name))));
                futures.add(saveBlockstate(cache, name, stairsBlockstate(
                        modBlock(name),
                        modBlock(name + "_inner"),
                        modBlock(name + "_outer")
                )));
            }

            {
                String name = rlPath(e.wall());
                futures.add(saveModel(cache, name + "_post",      wallPostModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_side",      wallSideModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_side_tall", wallSideTallModel(resolvedTex)));
                futures.add(saveModel(cache, name + "_inventory", wallInventoryModel(resolvedTex)));
                futures.add(saveItemModel(cache, name, itemParent(modBlock(name + "_inventory"))));
                futures.add(saveBlockstate(cache, name, wallBlockstate(
                        modBlock(name + "_post"),
                        modBlock(name + "_side"),
                        modBlock(name + "_side_tall")
                )));
            }
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override public String getName() { return "ChippedExttras Models & Blockstates (optimized)"; }

    private static String resolveBestTexture(ResourceLocation baseBlock) {
        if (baseBlock == null) return "minecraft:block/stone";
        String cached = TEX_CACHE.get(baseBlock);
        if (cached != null) return cached;

        String direct = "assets/" + baseBlock.getNamespace() + "/textures/block/" + baseBlock.getPath() + ".png";
        if (classpathExists(direct)) {
            String v = baseBlock.getNamespace() + ":block/" + baseBlock.getPath();
            TEX_CACHE.put(baseBlock, v);
            return v;
        }
        String modelRef = "assets/" + baseBlock.getNamespace() + "/models/block/" + baseBlock.getPath() + ".json";
        String tex = findTextureRecursive(modelRef, new HashSet<>());
        TEX_CACHE.put(baseBlock, tex);
        return tex;
    }

    private static String findTextureRecursive(String modelPath, Set<String> seen) {
        if (!seen.add(modelPath)) return "minecraft:block/stone";
        JsonObject model = readJsonCached(modelPath);
        if (model == null) return "minecraft:block/stone";

        String tex = firstConcreteTexture(model, "all","wall","side","top","bottom","end","texture","0","1","particle");
        if (tex != null) return tex;

        if (model.has("parent")) {
            String parent = model.get("parent").getAsString();
            String parentPath = "assets/" + parent.replace(':','/').replaceFirst("/", "/models/") + ".json";
            return findTextureRecursive(parentPath, seen);
        }
        return "minecraft:block/stone";
    }

    private static String firstConcreteTexture(JsonObject model, String... keys) {
        if (!model.has("textures")) return null;
        JsonObject textures = model.getAsJsonObject("textures");
        for (String k : keys) {
            if (textures.has(k)) {
                JsonElement el = textures.get(k);
                if (el.isJsonPrimitive()) {
                    String s = el.getAsString();
                    if (!s.startsWith("#") && !s.isBlank()) return s;
                }
            }
        }
        return null;
    }

    private static boolean classpathExists(String path) {
        if (MISSING_RES.contains(path)) return false;
        boolean ok = VariantModelStateProvider.class.getClassLoader().getResource(path) != null;
        if (!ok) MISSING_RES.add(path);
        return ok;
    }
    private static JsonObject readJsonCached(String path) {
        JsonObject cached = MODEL_JSON_CACHE.get(path);
        if (cached != null) return cached;
        try (InputStream in = VariantModelStateProvider.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) return null;
            try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                JsonObject obj = GSON.fromJson(r, JsonObject.class);
                MODEL_JSON_CACHE.put(path, obj);
                return obj;
            }
        } catch (Exception ignored) { return null; }
    }

    private CompletableFuture<?> saveModel(CachedOutput cache, String name, Map<String, Object> json) {
        Path p = output.getOutputFolder().resolve("assets/" + Chippedexttras.MODID + "/models/block/" + name + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), p);
    }
    private CompletableFuture<?> saveItemModel(CachedOutput cache, String name, Map<String, Object> json) {
        Path p = output.getOutputFolder().resolve("assets/" + Chippedexttras.MODID + "/models/item/" + name + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), p);
    }
    private CompletableFuture<?> saveBlockstate(CachedOutput cache, String name, Map<String, Object> json) {
        Path p = output.getOutputFolder().resolve("assets/" + Chippedexttras.MODID + "/blockstates/" + name + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), p);
    }
    private static String rlPath(String rl) { return ResourceLocation.tryParse(rl).getPath(); }
    private static String modBlock(String name) { return Chippedexttras.MODID + ":block/" + name; }

    private static Map<String, Object> itemParent(String parent) { return map("parent", parent); }
    private static Map<String, Object> cubeAllModel(String tex) {
        return map("parent","minecraft:block/cube_all","textures",map("all",tex,"particle",tex));
    }
    private static Map<String, Object> slabModel(String tex) {
        return map("parent","minecraft:block/slab","textures",map("side",tex,"bottom",tex,"top",tex,"particle",tex));
    }
    private static Map<String, Object> slabTopModel(String tex) {
        return map("parent","minecraft:block/slab_top","textures",map("side",tex,"bottom",tex,"top",tex,"particle",tex));
    }
    private static Map<String, Object> stairsModel(String tex) {
        return map("parent","minecraft:block/stairs","textures",map("bottom",tex,"top",tex,"side",tex,"particle",tex));
    }
    private static Map<String, Object> stairsInnerModel(String tex) {
        return map("parent","minecraft:block/inner_stairs","textures",map("bottom",tex,"top",tex,"side",tex,"particle",tex));
    }
    private static Map<String, Object> stairsOuterModel(String tex) {
        return map("parent","minecraft:block/outer_stairs","textures",map("bottom",tex,"top",tex,"side",tex,"particle",tex));
    }

    // In-world templates for walls
    private static Map<String, Object> wallPostModel(String tex) {
        return map("parent","minecraft:block/template_wall_post","textures",map("wall",tex,"particle",tex));
    }
    private static Map<String, Object> wallSideModel(String tex) {
        return map("parent","minecraft:block/template_wall_side","textures",map("wall",tex,"particle",tex));
    }
    private static Map<String, Object> wallSideTallModel(String tex) {
        return map("parent","minecraft:block/template_wall_side_tall","textures",map("wall",tex,"particle",tex));
    }
    private static Map<String, Object> wallInventoryModel(String tex) {
        return map("parent","minecraft:block/wall_inventory","textures",map("wall",tex,"particle",tex));
    }

    /* ------------------------- blockstate builders ------------------------- */
    private static Map<String, Object> slabBlockstate(String modelBottom, String modelTop, String modelDouble) {
        Map<String, Object> variants = new LinkedHashMap<>();
        variants.put("type=bottom", map("model", modelBottom));
        variants.put("type=top",    map("model", modelTop));
        variants.put("type=double", map("model", modelDouble));
        return map("variants", variants);
    }


    private static Map<String, Object> stairsBlockstate(String model, String modelInner, String modelOuter) {
        Map<String, Object> variants = new LinkedHashMap<>();
        String[] facings = {"east","south","west","north"};
        String[] halves  = {"bottom","top"};
        String[] shapes  = {"straight","inner_left","inner_right","outer_left","outer_right"};
        Map<String,Integer> baseY = Map.of("east",0,"south",90,"west",180,"north",270);

        for (String half : halves) for (String facing : facings) for (String shape : shapes) {
            String key = "facing=" + facing + ",half=" + half + ",shape=" + shape;

            String use = switch (shape) {
                case "inner_left", "inner_right" -> modelInner;
                case "outer_left", "outer_right" -> modelOuter;
                default -> model;
            };

            int y = baseY.get(facing);
            if ("bottom".equals(half)) {
                if (shape.endsWith("left"))  y = (y + 270) % 360;
            } else { // top
                if (shape.endsWith("right")) y = (y + 90) % 360;
            }
            int x = "top".equals(half) ? 180 : 0;

            Map<String,Object> v = new LinkedHashMap<>();
            v.put("model", use);
            if (x != 0) v.put("x", x);
            if (y != 0) v.put("y", y);
            v.put("uvlock", true);
            variants.put(key, v);
        }
        return map("variants", variants);
    }

    private static Map<String, Object> wallBlockstate(String postModel, String sideModel, String tallModel) {
        List<Map<String,Object>> multipart = new ArrayList<>();
        multipart.add(map("when", map("up", true), "apply", map("model", postModel)));
        String[] dirs = {"north","east","south","west"};
        Map<String,Integer> dirRot = Map.of("north",0,"east",90,"south",180,"west",270);
        for (String dir : dirs) {
            multipart.add(map("when", map(dir,"low"),  "apply", map("model", sideModel, "y", dirRot.get(dir), "uvlock", true)));
            multipart.add(map("when", map(dir,"tall"), "apply", map("model", tallModel, "y", dirRot.get(dir), "uvlock", true)));
        }
        return map("multipart", multipart);
    }

    @SafeVarargs
    private static <K,V> Map<K,V> map(Object... kv) {
        Map<K,V> m = new LinkedHashMap<>();
        for (int i=0; i<kv.length; i+=2) {
            @SuppressWarnings("unchecked") K k = (K) kv[i];
            @SuppressWarnings("unchecked") V v = (V) kv[i+1];
            m.put(k, v);
        }
        return m;
    }
}
