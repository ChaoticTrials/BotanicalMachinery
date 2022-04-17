package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.network.BotanicalMachineryNetwork;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import io.github.noeppi_noeppi.libx.mod.registration.RegistrationBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

@Mod("botanicalmachinery")
public final class BotanicalMachinery extends ModXRegistration {

    private static BotanicalMachinery instance;
    private static BotanicalMachineryNetwork network;

    public BotanicalMachinery() {
        super(new CreativeModeTab("botanicalmachinery") {
            @Nonnull
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(ModBlocks.mechanicalManaPool);
            }
        });

        instance = this;
        network = new BotanicalMachineryNetwork(this);
    }

    @Nonnull
    public static BotanicalMachinery getInstance() {
        return instance;
    }

    @Nonnull
    public static BotanicalMachineryNetwork getNetwork() {
        return network;
    }

    @Override
    protected void initRegistration(RegistrationBuilder builder) {
        builder.setVersion(1);
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {

    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {

    }
}
