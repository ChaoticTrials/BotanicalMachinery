package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import net.minecraft.tags.BlockTags;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.tags.CommonTagsProviderBase;

public class CommonTags extends CommonTagsProviderBase {

    public CommonTags(DatagenContext context) {
        super(context);
    }

    @Override
    public void setup() {
        this.block(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.manaEmeraldBlock);
        this.block(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.manaEmeraldBlock);
    }
}
