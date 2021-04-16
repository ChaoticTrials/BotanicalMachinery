package de.melanx.botanicalmachinery.compat;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.screens.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.integration.jei.*;

import javax.annotation.Nonnull;

@JeiPlugin
public class BotanicalMachineryPlugin implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(BotanicalMachinery.getInstance().modid, "jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ScreenAlfheimMarket.class, 77, 36, 22, 15, ElvenTradeRecipeCategory.UID);
        registration.addRecipeClickArea(ScreenMechanicalApothecary.class, 87, 65, 22, 15, PetalApothecaryRecipeCategory.UID);
        registration.addRecipeClickArea(ScreenMechanicalBrewery.class, 96, 48, 22, 15, BreweryRecipeCategory.UID);
        registration.addRecipeClickArea(ScreenMechanicalDaisy.class, 24, 16, 24, 48, PureDaisyRecipeCategory.UID);
        registration.addRecipeClickArea(ScreenMechanicalManaPool.class, 77, 36, 22, 15, ManaPoolRecipeCategory.UID);
        registration.addRecipeClickArea(ScreenMechanicalRunicAltar.class, 87, 65, 22, 15, RunicAltarRecipeCategory.UID);
        registration.addRecipeClickArea(ScreenIndustrialAgglomerationFactory.class, 73, 51, 30, 25, TerraPlateRecipeCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.alfheimMarket), ElvenTradeRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.mechanicalApothecary), PetalApothecaryRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.mechanicalBrewery), BreweryRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.mechanicalDaisy), PureDaisyRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.mechanicalManaPool), ManaPoolRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.mechanicalRunicAltar), RunicAltarRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.industrialAgglomerationFactory), TerraPlateRecipeCategory.UID);
    }
}
