

package me.puredoom.chippedexttras.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
        bus = Bus.MOD,
        modid = "chippedexttras"
)
public class ChippedExttrasDataGen {
    public ChippedExttrasDataGen() {
    }

    @SubscribeEvent
    public static void gather(GatherDataEvent evt) {
        DataGenerator gen = evt.getGenerator();
        PackOutput out = gen.getPackOutput();
        ExistingFileHelper efh = evt.getExistingFileHelper();
        ChippedDiscoveryProvider discover = new ChippedDiscoveryProvider(out);
        gen.addProvider(evt.includeServer(), discover);
        gen.addProvider(evt.includeClient(), new VariantModelStateProvider(out, efh, discover));
        gen.addProvider(evt.includeServer(), new VariantRecipeProvider(out, discover));
        gen.addProvider(evt.includeServer(), new VariantLootProvider(out, discover));
        gen.addProvider(evt.includeServer(), new VariantBlockTags(out, evt.getLookupProvider(), discover, efh));
        gen.addProvider(evt.includeClient(), new VariantLang(out, discover));
    }
}
