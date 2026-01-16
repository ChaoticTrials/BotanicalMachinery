package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.compat.MythicBotanyCompat;
import mythicbotany.register.ModItems;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.recipe.RecipeProviderBase;
import org.moddingx.libx.datagen.provider.recipe.crafting.CompressionExtension;
import org.moddingx.libx.datagen.provider.recipe.crafting.CraftingExtension;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.List;

public class MythicRecipes extends RecipeProviderBase implements CraftingExtension, CompressionExtension {

    public MythicRecipes(DatagenContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        this.shaped(
                ModBlocks.mechanicalManaInfuser,
                "eye",
                "xaz",
                "ese",
                'e', BotaniaTags.Items.INGOTS_ELEMENTIUM,
                'a', BotaniaItems.auraRingGreater,
                's', ModItems.alfsteelIngot,
                'x', BotaniaTags.Items.INGOTS_ELEMENTIUM,
                'y', BotaniaTags.Items.GEMS_DRAGONSTONE,
                'z', BotaniaItems.pixieDust
        );
    }

    @Override
    protected List<ICondition> conditions() {
        return List.of(
                new ModLoadedCondition(MythicBotanyCompat.MOD_ID)
        );
    }
}
