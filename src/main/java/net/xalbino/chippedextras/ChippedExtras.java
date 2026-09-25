package net.xalbino.chippedextras;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
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

@Mod(ChippedExtras.MODID)
public class ChippedExtras {
    public static final String MODID = "chippedextras";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    /** Creative tab: chippedextras_tab */
    public static final CreativeModeTab CHIPPEDEXTRAS_TAB = new CreativeModeTab("chippedextras") {
        @Override
        public ItemStack makeIcon() {
            if (GeneratedRegistry.VARIANT_ITEMS.size() > 4) {
                return new ItemStack(GeneratedRegistry.VARIANT_ITEMS.get(4).get());
            } else if (!GeneratedRegistry.VARIANT_ITEMS.isEmpty()) {
                return new ItemStack(GeneratedRegistry.VARIANT_ITEMS.get(0).get());
            }
            return new ItemStack(Items.STONE);
        }
    };

  //  public static final RegistryObject<Item> MY_ITEM = ITEMS.register("my_item", () -> new Item(new Item.Properties().tab(ChippedExtras.CHIPPEDEXTRAS_TAB)));

    public ChippedExtras() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        boolean isDataGen = Boolean.getBoolean("chippedextras.datagen");
        if (!isDataGen) {
            GeneratedRegistry.bootstrapFromJson(BLOCKS, ITEMS);
        } else {
            LOGGER.info("[chippedextras] Datagen run detected: skipping runtime bootstrapFromJson()");
        }

        BLOCKS.register(modBus);
        ITEMS.register(modBus);

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
