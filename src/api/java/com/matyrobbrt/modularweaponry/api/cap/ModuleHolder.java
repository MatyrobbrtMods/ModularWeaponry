package com.matyrobbrt.modularweaponry.api.cap;

import com.matyrobbrt.modularweaponry.api.module.ModuleStack;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public interface ModuleHolder {

    Capability<ModuleHolder> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    ModuleStack getModule();

    void decreaseLevel(int toDecrease);

    default float getConsumedProbability() {
        return 1;
    }

    /**
     * A slot that only allows module holders to be put in it.
     * 
     * @author matyrobbrt
     *
     */
    class Slot extends net.minecraft.world.inventory.Slot {

        public Slot(Container pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            // Only allow module holders.
            return pStack.getCapability(CAPABILITY).isPresent();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

    }
}
