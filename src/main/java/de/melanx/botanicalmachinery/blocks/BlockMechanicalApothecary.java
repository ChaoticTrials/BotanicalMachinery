package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMechanicalApothecary;
import de.melanx.botanicalmachinery.blocks.tiles.TileMechanicalApothecary;
import de.melanx.botanicalmachinery.core.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockMechanicalApothecary extends Block {
    public BlockMechanicalApothecary() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(2, 10));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileMechanicalApothecary();
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);

            ItemStack held = player.getHeldItemMainhand();
            FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainer(held, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).orElse(null), 1000, player, true);
            if (fluidActionResult.isSuccess()) {
                if (!player.isCreative()) {
                    player.addItemStackToInventory(fluidActionResult.getResult());
                    held.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }

            if (tile instanceof TileMechanicalApothecary) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen." + BotanicalMachinery.MODID + "." + LibNames.MECHANICAL_APOTHECARY);
                    }

                    @Nonnull
                    @Override
                    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
                        return new ContainerMechanicalApothecary(windowId, world, pos, playerInventory, player);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
