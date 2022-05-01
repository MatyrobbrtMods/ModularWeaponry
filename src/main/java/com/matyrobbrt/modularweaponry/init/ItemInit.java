package com.matyrobbrt.modularweaponry.init;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.item.ModularPickaxe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
        ModularWeaponry.MOD_ID);

    public static final RegistryObject<ModularPickaxe> MODULAR_PICKAXE = ITEMS.register("modular_pickaxe",
        () -> new ModularPickaxe(Tiers.DIAMOND, 0, 0, new Item.Properties()));
}
