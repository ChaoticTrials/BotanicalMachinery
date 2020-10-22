package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import io.github.noeppi_noeppi.libx.data.provider.ItemModelProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProviderBase {

    public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(BotanicalMachinery.getInstance(), gen, helper);
    }

    @Override
    protected void setup() {

    }
}
