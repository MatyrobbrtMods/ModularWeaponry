package com.matyrobbrt.modularweaponry.init;

import java.util.function.Supplier;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.modulebench.ModuleBenchBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockInit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
        ModularWeaponry.MOD_ID);

    //@formatter:off
    public static final RegistryObject<ModuleBenchBlock> MODULE_BENCH = register(
        "module_bench",
        () -> new ModuleBenchBlock(Properties.of(Material.WOOD).requiresCorrectToolForDrops().strength(5f)),
        new Item.Properties().stacksTo(4).tab(ModularWeaponry.TAB)
    );

    private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> block,
        Item.Properties itemProperties) {
        final var blockR = BLOCKS.register(name, block);
        ItemInit.ITEMS.register(name, () -> new BlockItem(blockR.get(), itemProperties));
        return blockR;
    }
}
