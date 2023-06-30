package de.melanx.botanicalmachinery;

import net.minecraft.world.item.Item;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.base.ItemBase;

@RegisterClass(registry = "ITEM_REGISTRY")
public class ModItems {

    public static final Item manaEmerald = new ItemBase(BotanicalMachinery.getInstance(), new Item.Properties());
}
