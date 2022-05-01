package com.matyrobbrt.modularweaponry.init;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.modulebench.ModuleBenchBE;

import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BETypeInit {

    public static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister
        .create(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, ModularWeaponry.MOD_ID);

    public static final RegistryObject<BlockEntityType<ModuleBenchBE>> MODULE_BENCH = BE_TYPES.register("module_bench",
        () -> BlockEntityType.Builder.of(ModuleBenchBE::new, BlockInit.MODULE_BENCH.get()).build(null));
}
