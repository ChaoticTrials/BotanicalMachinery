package de.melanx.botanicalmachinery;

import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import io.github.noeppi_noeppi.libx.base.ItemBase;
import net.minecraft.world.item.Item;

@RegisterClass
public class ModItems {

    public static final Item manaEmerald = new ItemBase(BotanicalMachinery.getInstance(), new Item.Properties());
}
