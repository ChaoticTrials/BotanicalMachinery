package de.melanx.botanicalmachinery.core;

import de.melanx.botanicalmachinery.core.registration.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ModGroup extends ItemGroup {
    public ModGroup(String label) {
        super(label);
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.MECHANICAL_MANA_POOL);
    }
}
