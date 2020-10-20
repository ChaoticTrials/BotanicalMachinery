package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.core.LibNames;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.item.Item;

public class ModItems {

    public static final Item manaEmerald = new ItemBase(BotanicalMachinery.getInstance(), new Item.Properties());

    public static void register() {
        BotanicalMachinery.getInstance().register(LibNames.MANA_EMERALD, manaEmerald);
    }
}
