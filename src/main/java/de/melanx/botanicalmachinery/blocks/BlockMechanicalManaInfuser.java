package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaInfuser;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaInfuser;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalManaInfuserRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaInfuser;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

public class BlockMechanicalManaInfuser extends BotanicalBlock<BlockEntityMechanicalManaInfuser, ContainerMenuMechanicalManaInfuser> {

    public BlockMechanicalManaInfuser(ModX mod, Class<BlockEntityMechanicalManaInfuser> teClass, MenuType<ContainerMenuMechanicalManaInfuser> menu) {
        super(mod, teClass, menu, false, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.mechanicalManaInfuser.menu, ScreenMechanicalManaInfuser::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalManaInfuserRenderer());
    }
}
