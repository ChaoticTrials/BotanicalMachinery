package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalDaisy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BlockMechanicalDaisy extends Block {

    public BlockMechanicalDaisy(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileMechanicalDaisy();
    }
}
