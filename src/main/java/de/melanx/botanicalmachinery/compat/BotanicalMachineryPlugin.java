package de.melanx.botanicalmachinery.compat;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaPool;
import de.melanx.botanicalmachinery.core.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.integration.jei.manapool.ManaPoolRecipeCategory;

@JeiPlugin
public class BotanicalMachineryPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(BotanicalMachinery.MODID, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ScreenMechanicalManaPool.class, 77, 36, 22, 15, ManaPoolRecipeCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Registration.BLOCK_MECHANICAL_MANA_POOL.get()), ManaPoolRecipeCategory.UID);
    }
}
