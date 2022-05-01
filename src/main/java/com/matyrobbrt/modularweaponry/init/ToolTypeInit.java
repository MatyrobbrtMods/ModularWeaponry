package com.matyrobbrt.modularweaponry.init;

import java.util.function.Supplier;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.ToolType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class ToolTypeInit {

    public static final DeferredRegister<ToolType> TOOL_TYPES = DeferredRegister.create(ToolType.REGISTRY_KEY,
        ModularWeaponry.MOD_ID);

    public static final Supplier<IForgeRegistry<ToolType>> REGISTRY = TOOL_TYPES.makeRegistry(ToolType.class,
        () -> new RegistryBuilder<ToolType>().disableOverrides());

    public static final RegistryObject<ToolType> PICKAXE = TOOL_TYPES.register("pickaxe", ToolType::new);
    public static final RegistryObject<ToolType> SWORD = TOOL_TYPES.register("sword", ToolType::new);

}
