package me.puredoom.chippedexttras;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mod(Chippedexttras.MODID)
public class Chippedexttras {
    public static final String MODID = "chippedexttras";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item>  ITEMS  = DeferredRegister.create(ForgeRegistries.ITEMS,  MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    /** Creative tab: chippedexttras_tab */
    public static final RegistryObject<CreativeModeTab> CHIPPEDEXTTRAS_TAB =
            TABS.register("chippedexttras_tab", () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS)
                    .title(Component.translatable("itemGroup." + MODID + ".chippedexttras_tab"))
                    .icon(() -> new ItemStack(Blocks.STONE_BRICK_STAIRS))
                    .displayItems((params, out) -> {
                        List<Item> ours = new ArrayList<>();
                        Set<String> seen = new LinkedHashSet<>();

                        for (RegistryObject<Item> ro : GeneratedRegistry.VARIANT_ITEMS) {
                            Item it = ro.get();
                            if (it instanceof BlockItem bi) {
                                var id = ForgeRegistries.ITEMS.getKey(bi);
                                if (id != null && MODID.equals(id.getNamespace()) && seen.add(id.toString())) {
                                    ours.add(bi);
                                }
                            }
                        }
                        for (RegistryObject<Item> ro : ITEMS.getEntries()) {
                            Item it = ro.get();
                            if (it instanceof BlockItem bi) {
                                var id = ForgeRegistries.ITEMS.getKey(bi);
                                if (id != null && MODID.equals(id.getNamespace()) && seen.add(id.toString())) {
                                    ours.add(bi);
                                }
                            }
                        }

                        ours.sort(Comparator.comparing(i -> ForgeRegistries.ITEMS.getKey(i).toString()));
                        ours.forEach(out::accept);
                    })
                    .build());

    public Chippedexttras() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        boolean isDataGen = Boolean.getBoolean("chippedexttras.datagen");
        if (!isDataGen) {
            GeneratedRegistry.bootstrapFromJson(BLOCKS, ITEMS);
        } else {
            LOGGER.info("[chippedexttras] Datagen run detected: skipping runtime bootstrapFromJson()");
        }

        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        TABS.register(modBus);

        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent public void onServerStarting(ServerStartingEvent e) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent public static void onClientSetup(FMLClientSetupEvent e) { }
    }
}
