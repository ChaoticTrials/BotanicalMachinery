package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.ItemModelProviderBase;

@Datagen
public class ItemModels extends ItemModelProviderBase {

    public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(BotanicalMachinery.getInstance(), gen, helper);
    }

    @Override
    protected void setup() {
        // NO-OP
    }
}
