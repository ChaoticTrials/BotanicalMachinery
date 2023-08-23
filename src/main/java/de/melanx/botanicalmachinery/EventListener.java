package de.melanx.botanicalmachinery;

import de.melanx.botanicalmachinery.blocks.tiles.BlockEntityMechanicalManaPool;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "botanicalmachinery")
public class EventListener {

    @SubscribeEvent
    public static void resourcesReload(OnDatapackSyncEvent event) {
        BlockEntityMechanicalManaPool.invalidateCatalysts();
    }
}
