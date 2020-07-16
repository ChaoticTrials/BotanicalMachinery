package de.melanx.botanicalmachinery.blocks.tiles;

/**
 * All machines which uses mana and items to create something else via recipe.
 */
public interface IManaMachineTile {
    /**
     * @return True if the recipe in the machine is valid or if no recipe is given.
     */
    default boolean hasValidRecipe() {
        return true;
    }

    /**
     * @return Maximum mana amount in machine.
     */
    int getManaCap();
}
