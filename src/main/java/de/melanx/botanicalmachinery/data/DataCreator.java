package de.melanx.botanicalmachinery.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataCreator {

    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            gen.addProvider(new LootTables(gen));
            gen.addProvider(new Recipes(gen));
        }
        if (event.includeClient()) {
            gen.addProvider(new BlockStates(gen, helper));
            gen.addProvider(new ItemModels(gen, helper));
        }
    }
}
