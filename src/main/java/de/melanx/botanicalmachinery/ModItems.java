package de.melanx.botanicalmachinery;

import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.item.Item;

@RegisterClass
public class ModItems {

    public static final Item manaEmerald = new ItemBase(BotanicalMachinery.getInstance(), new Item.Properties());
}
