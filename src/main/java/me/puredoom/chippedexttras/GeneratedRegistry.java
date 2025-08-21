package me.puredoom.chippedexttras;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.puredoom.chippedexttras.block.FixedSlabBlock;
import me.puredoom.chippedexttras.block.FixedStairBlock;
import me.puredoom.chippedexttras.block.FixedWallBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

public class GeneratedRegistry {

    public static final List<RegistryObject<Item>> VARIANT_ITEMS = new ArrayList<>();

    public static class Entry {
        public String base;
        public String slab;
        public String stairs;
        public String wall;
    }

    public static void bootstrapFromJson(DeferredRegister<Block> blocks, DeferredRegister<Item> items) {
        final List<Entry> entries = loadEntries();
        if (entries.isEmpty()) {
            final boolean isDataGen = Boolean.getBoolean("chippedexttras.datagen");
            if (isDataGen) return;
            System.out.println("[chippedexttras] WARNING: registry.json missing/empty; skipping dynamic registration.");
            return;
        }

        entries.sort(Comparator
                .comparing((Entry x) -> String.valueOf(x.base))
                .thenComparing(x -> String.valueOf(x.slab))
                .thenComparing(x -> String.valueOf(x.stairs))
                .thenComparing(x -> String.valueOf(x.wall)));

        System.out.println("[chippedexttras] registering " + entries.size() + " variants");

        for (Entry e : entries) {
            final ResourceLocation baseRL = ResourceLocation.tryParse(e.base);
            final Block baseBlock = (baseRL != null) ? ForgeRegistries.BLOCKS.getValue(baseRL) : null;

            final MapColor color = MapColor.STONE;
            final SoundType sound = (baseBlock != null) ? baseBlock.defaultBlockState().getSoundType() : SoundType.STONE;

            BlockBehaviour.Properties props = BlockBehaviour.Properties
                    .of()
                    .mapColor(color)
                    .strength(1.5F, 6.0F)
                    .sound(sound);

            // STAIRS
            if (e.stairs != null && !e.stairs.isEmpty()) {
                final String id = Objects.requireNonNull(ResourceLocation.tryParse(e.stairs)).getPath();
                final Block parent = (baseBlock != null ? baseBlock : Blocks.STONE);
                RegistryObject<Block> roB = blocks.register(id, () -> new FixedStairBlock(() -> parent.defaultBlockState(), props));
                RegistryObject<Item> roI = items.register(id, () -> new BlockItem(roB.get(), new Item.Properties()));
                VARIANT_ITEMS.add(roI);
            }

            // SLAB
            if (e.slab != null && !e.slab.isEmpty()) {
                final String id = Objects.requireNonNull(ResourceLocation.tryParse(e.slab)).getPath();
                RegistryObject<Block> roB = blocks.register(id, () -> new FixedSlabBlock(props));
                RegistryObject<Item> roI = items.register(id, () -> new BlockItem(roB.get(), new Item.Properties()));
                VARIANT_ITEMS.add(roI);
            }

            // WALL
            if (e.wall != null && !e.wall.isEmpty()) {
                final String id = Objects.requireNonNull(ResourceLocation.tryParse(e.wall)).getPath();
                RegistryObject<Block> roB = blocks.register(id, () -> new FixedWallBlock(props));
                RegistryObject<Item> roI = items.register(id, () -> new BlockItem(roB.get(), new Item.Properties()));
                VARIANT_ITEMS.add(roI);
            }
        }
    }

    private static List<Entry> loadEntries() {
        try (InputStream in = GeneratedRegistry.class.getResourceAsStream(
                "/assets/" + Chippedexttras.MODID + "/generated/registry.json")) {
            if (in == null) return Collections.emptyList();
            Type listType = new TypeToken<List<Entry>>() {}.getType();
            List<Entry> list = new Gson().fromJson(new InputStreamReader(in), listType);
            return (list != null) ? list : Collections.emptyList();
        } catch (Exception ex) {
            System.out.println("[chippedexttras] ERROR reading registry.json: " + ex);
            return Collections.emptyList();
        }
    }
}
