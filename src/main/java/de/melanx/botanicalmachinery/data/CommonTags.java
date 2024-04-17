package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.tags.CommonTagsProviderBase;

public class CommonTags extends CommonTagsProviderBase {

    public CommonTags(DatagenContext context) {
        super(context);
    }

    public static final TagKey<Item> MECHANICAL_APOTHECARY_CATALYSTS = TagKey.create(Registries.ITEM, BotanicalMachinery.getInstance().resource("mechanical_apothecary_catalysts"));

    @Override
    public void setup() {
        this.item(MECHANICAL_APOTHECARY_CATALYSTS).addTag(Tags.Items.SEEDS);

        this.block(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.manaEmeraldBlock);
    }

    @Override
    public void defaultBlockTags(Block block) {
        this.block(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
    }
}
