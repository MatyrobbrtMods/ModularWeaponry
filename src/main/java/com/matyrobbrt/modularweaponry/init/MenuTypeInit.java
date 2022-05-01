package com.matyrobbrt.modularweaponry.init;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.modulebench.ModuleBenchContainer;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuTypeInit {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS,
        ModularWeaponry.MOD_ID);

    public static final RegistryObject<MenuType<ModuleBenchContainer>> MODULE_BENCH = register("module_bench",
        ModuleBenchContainer::new);

    private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name,
        IContainerFactory<C> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
}
