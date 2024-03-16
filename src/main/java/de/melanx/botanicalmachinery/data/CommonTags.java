package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.CommonTagsProviderBase;
import org.moddingx.libx.mod.ModX;

@Datagen
public class CommonTags extends CommonTagsProviderBase {

    public static final TagKey<Item> MECHANICAL_APOTHECARY_CATALYSTS = TagKey.create(Registry.ITEM_REGISTRY, BotanicalMachinery.getInstance().resource("mechanical_apothecary_catalysts"));
    
    public CommonTags(ModX mod, DataGenerator generator, ExistingFileHelper fileHelper) {
        super(mod, generator, fileHelper);
    }

    @Override
    public void setup() {
        this.item(MECHANICAL_APOTHECARY_CATALYSTS).addTag(Tags.Items.SEEDS);

        this.block(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.manaEmeraldBlock);
        this.block(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.manaEmeraldBlock);
    }
}
