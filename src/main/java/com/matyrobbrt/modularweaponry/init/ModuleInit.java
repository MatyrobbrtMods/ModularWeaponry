package com.matyrobbrt.modularweaponry.init;

import java.util.function.Supplier;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.module.Module;
import com.matyrobbrt.modularweaponry.module.LightningAttackModule;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class ModuleInit {

    public static final DeferredRegister<Module> MODULES = DeferredRegister.create(Module.REGISTRY_KEY,
        ModularWeaponry.MOD_ID);

    public static final Supplier<IForgeRegistry<Module>> REGISTRY = MODULES.makeRegistry(Module.class,
        () -> new RegistryBuilder<Module>().disableOverrides().setDefaultKey(Module.EMPTY.getId()).hasTags());

    public static final RegistryObject<Module> EMPTY = MODULES.register(Module.EMPTY.getId().getPath(), Module::new);
    public static final RegistryObject<Module> LIGHTNING_ATTACK = MODULES.register("lightning_attack",
        LightningAttackModule::new);

    public static final RegistryObject<Module> TEST = MODULES.register("test", Module::new);
}
