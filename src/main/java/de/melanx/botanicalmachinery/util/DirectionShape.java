package de.melanx.botanicalmachinery.util;

import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class DirectionShape {

    private final VoxelShape north;
    private final VoxelShape south;
    private final VoxelShape east;
    private final VoxelShape west;

    public DirectionShape(VoxelShape baseShape) {
        this.north = baseShape.simplify();
        this.east = rotated(this.north);
        this.south = rotated(this.east);
        this.west = rotated(this.south);
    }

    public VoxelShape getShape(Direction direction) {
        switch (direction) {
            case NORTH:
                return this.north;
            case SOUTH:
                return this.south;
            case WEST:
                return this.west;
            case EAST:
                return this.east;
            default:
                return this.north;
        }
    }

    private static VoxelShape rotated(VoxelShape src) {
        List<VoxelShape> boxes = new ArrayList<>();
        src.forEachBox((fromX, fromY, fromZ, toX, toY, toZ) -> boxes.add(VoxelShapes.create(1 - fromZ, fromY, fromX, 1 - toZ, toY, toX)));
        return VoxelShapes.or(VoxelShapes.empty(), boxes.toArray(new VoxelShape[]{})).simplify();
    }
}
