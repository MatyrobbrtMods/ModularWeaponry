package com.matyrobbrt.modularweaponry.api.module;

import java.util.List;
import java.util.function.Supplier;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.ToolType;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;

// TODO add module weigh, also add Properties, like the item has
@SuppressWarnings("static-method")
public class Module extends ForgeRegistryEntry<Module> {

    public static final ResourceKey<Registry<Module>> REGISTRY_KEY = ResourceKey
        .createRegistryKey(ModularWeaponry.rl("module"));

    public static final Supplier<IForgeRegistry<Module>> REGISTRY = Lazy
        .concurrentOf(() -> RegistryManager.ACTIVE.getRegistry(REGISTRY_KEY));

    public static final RegistryObject<Module> EMPTY = RegistryObject
        .create(new ResourceLocation("modularweaponry", "empty"), REGISTRY_KEY, "modularweaponry");

    public final Lazy<List<Module>> incompatibleModules = Lazy.concurrentOf(
        () -> REGISTRY.get().getValues().stream().filter(m -> m != this && !this.isCompatibleWith(m)).toList());

    public void onPlayerAttackTarget(final Player player, final Entity target, final ModuleStack module) {

    }

    public int getMaxLevel() {
        return 5;
    }

    public boolean isCompatibleWith(Module other) {
        return true;
    }

    public boolean isCompatibleWithTool(ToolType toolType) {
        return true;
    }

    public Component getName() {
        return new TranslatableComponent(Util.makeDescriptionId(ModularWeaponry.MOD_ID + "_module", getRegistryName()));
    }

    public Component getDescription() {
        return new TranslatableComponent(
            "description." + Util.makeDescriptionId(ModularWeaponry.MOD_ID + "_module", getRegistryName()));
    }

    public void whenActivated(final Player player, final ItemStack itemStack, final ModuleStack module) {

    }

    public boolean isActive() {
        return false;
    }

    public boolean holdToActivate() {
        return true;
    }

    public int getLevelWeight() {
        return 3;
    }

    public final ModuleStack createStack(int level) {
        return new ModuleStack(this, level);
    }
}
