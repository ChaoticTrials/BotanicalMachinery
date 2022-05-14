package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuManaBattery;
import de.melanx.botanicalmachinery.blocks.screens.ScreenManaBattery;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityManaBattery;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

public class BlockManaBattery extends BotanicalBlock<BlockEntityManaBattery, ContainerMenuManaBattery> {

    public final Variant variant;

    public BlockManaBattery(ModX mod, Class<BlockEntityManaBattery> teClass, MenuType<ContainerMenuManaBattery> menu, Variant variant) {
        super(mod, teClass, menu, true, false);
        this.variant = variant;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        super.registerClient(id, defer);
        ItemStackRenderer.addRenderBlock(this.getBlockEntityType(), true);
        MenuScreens.register(this.menu, ScreenManaBattery::new);
    }

    public enum Variant {
        CREATIVE,
        NORMAL
    }
}
