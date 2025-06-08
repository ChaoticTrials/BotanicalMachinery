package de.melanx.botanicalmachinery.compat;

import de.melanx.botanicalmachinery.ModBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import vazkii.botania.client.integration.emi.BotaniaEmiPlugin;

@EmiEntrypoint
public class BotanicalEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addWorkstation(BotaniaEmiPlugin.PETAL_APOTHECARY, EmiStack.of(ModBlocks.mechanicalApothecary));
        registry.addWorkstation(BotaniaEmiPlugin.MANA_INFUSION, EmiStack.of(ModBlocks.mechanicalManaPool));
        registry.addWorkstation(BotaniaEmiPlugin.RUNIC_ALTAR, EmiStack.of(ModBlocks.mechanicalRunicAltar));
        registry.addWorkstation(BotaniaEmiPlugin.TERRESTRIAL_AGGLOMERATION, EmiStack.of(ModBlocks.industrialAgglomerationFactory));
        registry.addWorkstation(BotaniaEmiPlugin.ELVEN_TRADE, EmiStack.of(ModBlocks.alfheimMarket));
        registry.addWorkstation(BotaniaEmiPlugin.BOTANICAL_BREWERY, EmiStack.of(ModBlocks.mechanicalBrewery));
        registry.addWorkstation(BotaniaEmiPlugin.PURE_DAISY, EmiStack.of(ModBlocks.mechanicalDaisy));
    }
}
