package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BaseBlock;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBattery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockManaBattery extends BaseBlock {

    public final Variant variant;

    public BlockManaBattery(Variant variant) {
        super(true);
        this.variant = variant;
    }

    public enum Variant {
        CREATIVE,
        NORMAL
    }

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

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull BlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
        TileManaBattery tile = (TileManaBattery) worldIn.getTileEntity(pos);
        return tile != null ? tile.getCurrentMana() / tile.getManaCap() * 15 : 0;
    }
}
