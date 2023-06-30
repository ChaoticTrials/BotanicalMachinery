package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuManaBattery;
import de.melanx.botanicalmachinery.blocks.screens.ScreenManaBattery;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;
import org.moddingx.libx.render.ItemStackRenderer;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockManaBattery extends BotanicalBlock<BlockEntityManaBattery, ContainerMenuManaBattery> {

    public final Variant variant;

    public BlockManaBattery(ModX mod, Class<BlockEntityManaBattery> teClass, MenuType<ContainerMenuManaBattery> menu, Variant variant) {
        super(mod, teClass, menu, true, false);
        this.variant = variant;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        ItemStackRenderer.addRenderBlock(this.getBlockEntityType(), true);
        MenuScreens.register(this.menu, ScreenManaBattery::new);
    }

    @Override
    public void initializeItemClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        // NO-OP
    }

    public enum Variant {
        CREATIVE,
        NORMAL
    }
}
