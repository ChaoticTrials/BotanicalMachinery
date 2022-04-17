package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.CommonTagsProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

@Datagen
public class CommonTags extends CommonTagsProviderBase {
    
    public CommonTags(ModX mod, DataGenerator generator, ExistingFileHelper fileHelper) {
        super(mod, generator, fileHelper);
    }

    @Override
    public void setup() {
        this.block(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.manaEmeraldBlock);
        this.block(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.manaEmeraldBlock);
    }
}
