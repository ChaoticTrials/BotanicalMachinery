package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BlockBase;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockManaBattery extends BlockBase {

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull IBlockReader worldIn) {
        return new TileManaBattery();
    }

    @Nullable
    @Override
    protected ContainerType<?> getContainerType() {
        return Registration.CONTAINER_MANA_BATTERY.get();
    }
}
