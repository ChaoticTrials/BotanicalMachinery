package de.melanx.botanicalmachinery.data;

import com.google.gson.JsonObject;
import de.melanx.botanicalmachinery.BotanicalMachinery;
import de.melanx.botanicalmachinery.core.registration.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.state.Property;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.crafting.ModRecipeTypes;
import vazkii.botania.common.crafting.StateIngredientHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ManaInfusionProvider extends RecipeProvider {
    public ManaInfusionProvider(DataGenerator gen) {
        super(gen);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Botanical Machinery mana pool recipes";
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        registerInfusionRecipe(consumer, ModItems.MANA_EMERALD, Ingredient.fromTag(Tags.Items.GEMS_EMERALD), 8000);
        registerInfusionRecipe(consumer, de.melanx.botanicalmachinery.core.registration.ModBlocks.MANA_EMERALD, Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD), 8000 * 9);
    }

    private static ResourceLocation id(String s) {
        return new ResourceLocation(BotanicalMachinery.getInstance().modid, "mana_infusion/" + s);
    }

    private static void registerInfusionRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider output, Ingredient input, int mana) {
        @SuppressWarnings("ConstantConditions")
        String name = output.asItem().getRegistryName().getPath();
        consumer.accept(new FinishedRecipe(id(name), new ItemStack(output), input, mana));
    }

    private static class FinishedRecipe implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient input;
        private final ItemStack output;
        private final int mana;
        private final String group;
        @Nullable
        private final BlockState catalyst;

        public static FinishedRecipe conjuration(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
            return new FinishedRecipe(id, output, input, mana, "", ModBlocks.conjurationCatalyst.getDefaultState());
        }

        public static FinishedRecipe alchemy(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
            return alchemy(id, output, input, mana, "");
        }

        public static FinishedRecipe alchemy(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
            return new FinishedRecipe(id, output, input, mana, group, ModBlocks.alchemyCatalyst.getDefaultState());
        }

        public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
            this(id, output, input, mana, "");
        }

        public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
            this(id, output, input, mana, group, null);
        }

        public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group, @Nullable BlockState catalyst) {
            this.id = id;
            this.input = input;
            this.output = output;
            this.mana = mana;
            this.group = group;
            this.catalyst = catalyst;
        }

        @Override
        public void serialize(JsonObject json) {
            json.add("input", this.input.serialize());
            json.add("output", ItemNBTHelper.serializeStack(this.output));
            json.addProperty("mana", this.mana);
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            if (this.catalyst != null) {
                json.add("catalyst", StateIngredientHelper.serializeBlockState(this.catalyst));
            }
        }

        @SuppressWarnings("unchecked")
        private static <T extends Comparable<T>> String getName(Property<T> prop, Comparable<?> val) {
            return prop.getName((T) val);
        }

        @Nonnull
        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ModRecipeTypes.MANA_INFUSION_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return null;
        }
    }

}
