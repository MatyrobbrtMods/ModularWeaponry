package com.matyrobbrt.modularweaponry.api;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.module.Module;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import net.minecraftforge.registries.ForgeRegistryEntry;

@SuppressWarnings("static-method")
public class ToolType extends ForgeRegistryEntry<ToolType> {

    public static final ResourceKey<Registry<ToolType>> REGISTRY_KEY = ResourceKey
        .createRegistryKey(ModularWeaponry.rl("tool_type"));

    public boolean isCompatibleWithItem(Item item) {
        return true;
    }

    public boolean isCompatibleWithModule(Module module) {
        return true;
    }

    public int getMaxModules() {
        return 5;
    }

}
