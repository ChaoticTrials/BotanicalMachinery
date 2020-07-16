package de.melanx.botanicalmachinery.data;

import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.RegistryObject;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, BotanicalMachinery.MODID, helper);
    }

    @Override
    protected void registerModels() {
        for (RegistryObject<Item> item : Registration.ITEMS.getEntries()) {
            if (item.get() instanceof BlockItem)
                generateBlockItem(item.get());
            else
                generateItem(item.get());
        }
    }

    private void generateBlockItem(Item block) {
        String path = block.getRegistryName().getPath();
        getBuilder(path)
                .parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    private void generateItem(Item item) {
        String path = item.getRegistryName().getPath();
        getBuilder(path).parent(getExistingFile(mcLoc("item/handheld")))
                .texture("layer0", "item/" + path);
    }
}
