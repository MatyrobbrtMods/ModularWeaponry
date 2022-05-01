package com.matyrobbrt.modularweaponry.api.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class Result {

    private Result(final boolean success) {
        this.success = success;
    }

    /**
     * Result for when this recipe is not craftable, but has no message to display
     */
    public static final Result PASS = new Result(false);

    /**
     * Result containing the item result
     * 
     * @param  result Item result of this recipe
     * @return        Validation result with the given message
     */
    public static Result success(ItemStack result) {
        return new Result.Success(result);
    }

    /**
     * Result for when this recipe is not craftable and has an error message
     * 
     * @param  translationKey Error message translation key
     * @param  params         Arguments to format into the translation key
     * @return                Validation result with the given message
     */
    public static Result failure(String translationKey, Object... params) {
        return new Result.Failure(translationKey, params);
    }

    /** If true, this recipe passed and can be crafted for the given input */
    private final boolean success;

    public boolean getSuccess() {
        return success;
    }

    /**
     * Gets the item result
     * 
     * @return Item result
     */
    public ItemStack getResult() {
        return ItemStack.EMPTY;
    }

    /**
     * If true, this recipe failed with an error message. This message should be
     * displayed on screen
     * 
     * @return true if the recipe failed with an error message
     */
    public boolean hasError() {
        return false;
    }

    /**
     * Returns the message for this result
     * 
     * @return                               result message
     * @throws UnsupportedOperationException if this result is success or pass
     */
    public Component getMessage() {
        throw new UnsupportedOperationException("Cannot show error message on success");
    }

    /** Class for success, which has an item stack */
    private static class Success extends Result {

        private final ItemStack result;

        private Success(ItemStack result) {
            super(true);
            this.result = result;
        }

        @Override
        public ItemStack getResult() {
            return result;
        }
    }

    /** Class for failure, which has a message */
    private static class Failure extends Result {

        private final Component message;

        private Failure(String translationKey, Object[] params) {
            super(false);
            this.message = new TranslatableComponent(translationKey, params);
        }

        @Override
        public boolean hasError() {
            return true;
        }

        @Override
        public Component getMessage() {
            return message;
        }
    }

}
