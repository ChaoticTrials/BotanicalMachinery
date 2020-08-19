package de.melanx.botanicalmachinery.blocks.tiles;

import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.tileentity.ITickableTileEntity;
import vazkii.botania.common.block.tile.TileMod;

public class TileMechanicalApothecary extends TileMod implements ITickableTileEntity {

    public TileMechanicalApothecary() {
        super(Registration.TILE_MECHANICAL_APOTHECARY.get());
    }

    @Override
    public void tick() {

    }
}
