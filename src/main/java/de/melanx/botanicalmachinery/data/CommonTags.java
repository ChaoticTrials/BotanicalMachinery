package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.CommonTagsProviderBase;
import org.moddingx.libx.mod.ModX;

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
