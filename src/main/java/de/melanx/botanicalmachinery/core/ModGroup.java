package de.melanx.botanicalmachinery.core;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModGroup extends ItemGroup {
    public ModGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Registration.ITEM_MANA_BLOCK.get());
    }
}
