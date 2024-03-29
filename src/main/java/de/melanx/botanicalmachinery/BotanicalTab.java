package de.melanx.botanicalmachinery;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.creativetab.CreativeTabX;
import org.moddingx.libx.mod.ModX;

public class BotanicalTab extends CreativeTabX {

    public BotanicalTab(ModX mod) {
        super(mod);
    }

    @Override
    protected void buildTab(CreativeModeTab.Builder builder) {
        super.buildTab(builder);
        builder.title(Component.literal("Botanical Machinery"));
        builder.icon(() -> new ItemStack(ModBlocks.mechanicalManaPool));
    }

    @Override
    protected void addItems(TabContext ctx) {
        this.addModItems(ctx);
    }
}
