package de.melanx.botanicalmachinery.blocks;

import de.melanx.botanicalmachinery.ModBlocks;
import de.melanx.botanicalmachinery.blocks.base.BotanicalBlock;
import de.melanx.botanicalmachinery.blocks.containers.ContainerMenuMechanicalManaInfuser;
import de.melanx.botanicalmachinery.blocks.screens.ScreenMechanicalManaInfuser;
import de.melanx.botanicalmachinery.blocks.tesr.MechanicalManaInfuserRenderer;
import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaInfuser;
import de.melanx.botanicalmachinery.compat.MythicBotanyCompat;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.RegistrationContext;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;

public class BlockMechanicalManaInfuser extends BotanicalBlock<BlockEntityMechanicalManaInfuser, ContainerMenuMechanicalManaInfuser> {

    private final Item item;

    public BlockMechanicalManaInfuser(ModX mod, Class<BlockEntityMechanicalManaInfuser> teClass, MenuType<ContainerMenuMechanicalManaInfuser> menu) {
        super(mod, teClass, menu, false, true, null);
        this.item = new BlockItem(this, new Item.Properties()) {

            @Override
            public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
                return MythicBotanyCompat.isLoaded();
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerClient(SetupContext ctx) {
        super.registerClient(ctx);
        MenuScreens.register(ModBlocks.mechanicalManaInfuser.menu, ScreenMechanicalManaInfuser::new);
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new MechanicalManaInfuserRenderer());
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return MythicBotanyCompat.isLoaded();
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        super.registerAdditional(ctx, builder);
        builder.register(Registries.ITEM, this.item);
    }

    @Override
    public void initTracking(RegistrationContext ctx, TrackingCollector builder) throws ReflectiveOperationException {
        super.initTracking(ctx, builder);
        builder.track(ForgeRegistries.ITEMS, BlockMechanicalManaInfuser.class.getDeclaredField("item"));
    }
}
