
package me.puredoom.chippedexttras.datagen;

import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VariantBlockTags extends BlockTagsProvider {
    private final ChippedDiscoveryProvider d;

    public VariantBlockTags(PackOutput out, CompletableFuture<HolderLookup.Provider> lookupProvider, ChippedDiscoveryProvider d, ExistingFileHelper efh) {
        super(out, lookupProvider, "chippedexttras", efh);
        this.d = d;
    }

    protected void addTags(HolderLookup.Provider provider) {
        Iterator var2 = this.d.discovered().iterator();

        while(var2.hasNext()) {
            ChippedDiscoveryProvider.Entry e = (ChippedDiscoveryProvider.Entry)var2.next();
            this.addAll(e.slab(), BlockTags.SLABS);
            this.addAll(e.stairs(), BlockTags.STAIRS);
            this.addAll(e.wall(), BlockTags.WALLS);
            this.addAll(e.slab(), BlockTags.MINEABLE_WITH_PICKAXE);
            this.addAll(e.stairs(), BlockTags.MINEABLE_WITH_PICKAXE);
            this.addAll(e.wall(), BlockTags.MINEABLE_WITH_PICKAXE);
        }

    }

    private void addAll(String rl, TagKey<Block> tag) {
        this.tag(tag).addOptional(ResourceLocation.tryParse(rl));
    }

    public String getName() {
        return "ChippedExttras Block Tags";
    }
}
