

package net.xalbino.chippedextras.datagen;


import net.xalbino.chippedextras.ChippedExtras;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent; // Correct 1.18.2 Import
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
        bus = Bus.MOD,
        modid = ChippedExtras.MODID
)
public class ChippedExtrasDataGen {
    public ChippedExtrasDataGen() {
    }

    @SubscribeEvent
    public static void gather(GatherDataEvent evt) {
        DataGenerator gen = evt.getGenerator();
        ExistingFileHelper efh = evt.getExistingFileHelper();

        ChippedDiscoveryProvider discover = new ChippedDiscoveryProvider(gen);

        if (evt.includeServer()) {
            gen.addProvider(discover);
            gen.addProvider(new VariantRecipeProvider(gen, discover));
            gen.addProvider(new VariantLootProvider(gen, discover));
            gen.addProvider(new VariantBlockTags(gen, discover, efh));
        }

        if (evt.includeClient()) {
            gen.addProvider(new VariantModelStateProvider(gen, efh, discover));
            gen.addProvider(new VariantLang(gen, discover));
        }
    }
}
