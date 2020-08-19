package de.melanx.botanicalmachinery.core;

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
        return new ItemStack(Registration.ITEM_MECHANICAL_MANA_POOL.get());
    }
}
