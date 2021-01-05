package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBattery;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.inventory.container.ContainerType;

public class BlockManaBattery extends BotanicalBlock<TileManaBattery, ContainerManaBattery> {

    public final Variant variant;

    public BlockManaBattery(ModX mod, Class<TileManaBattery> teClass, ContainerType<ContainerManaBattery> container, Variant variant) {
        super(mod, teClass, container, true, false);
        this.variant = variant;
    }

    public enum Variant {
        CREATIVE,
        NORMAL
    }
}
