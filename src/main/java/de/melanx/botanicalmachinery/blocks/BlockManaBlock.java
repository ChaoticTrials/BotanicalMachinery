package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.blocks.containers.ContainerManaBlock;
import de.melanx.botanicalmachinery.blocks.tiles.TileManaBlock;
import de.melanx.botanicalmachinery.core.LibNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.botania.api.wand.IWandHUD;

import javax.annotation.Nullable;

public class BlockManaBlock extends Block implements ITileEntityProvider, IWandHUD {

    public BlockManaBlock() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(2, 10));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileManaBlock();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileManaBlock) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen." + BotanicalMachinery.MODID + "." + LibNames.MANA_BLOCK);
                    }

                    @Nullable
                    @Override
                    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
                        return new ContainerManaBlock(windowId, worldIn, pos, playerInventory, player);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return tile instanceof INamedContainerProvider ? (INamedContainerProvider) tile : null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public void renderHUD(Minecraft mc, World world, BlockPos pos) {
        ((TileManaBlock) world.getTileEntity(pos)).renderHUD(mc);
    }
}
