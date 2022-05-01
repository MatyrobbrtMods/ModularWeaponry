package com.matyrobbrt.modularweaponry.event;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.cap.ModularTool;
import com.matyrobbrt.modularweaponry.api.cap.ModuleHolder;
import com.matyrobbrt.modularweaponry.api.module.ModuleStack;
import com.matyrobbrt.modularweaponry.cap.OneCapabilityProvider;
import com.matyrobbrt.modularweaponry.init.ModuleInit;
import com.matyrobbrt.modularweaponry.init.ToolTypeInit;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TestEvents {

    @SubscribeEvent
    static void attachCaps(final AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof SwordItem sword) {
            final var cap = new ModularTool.Implementation(ToolTypeInit.SWORD.get(), 23);
            event.addCapability(ModularWeaponry.rl("modular_tool"),
                new OneCapabilityProvider<>(LazyOptional.of(() -> cap), ModularTool.CAPABILITY));
        } else if (event.getObject().getItem() == Items.GOLD_INGOT) {
            event.addCapability(ModularWeaponry.rl("module_holder"),
                new OneCapabilityProvider<>(LazyOptional.of(() -> new ModuleHolder() {

                    final ModuleStack module = new ModuleStack(ModuleInit.LIGHTNING_ATTACK.get(), 2);

                    @Override
                    public ModuleStack getModule() {
                        return module.immutable();
                    }

                    @Override
                    public void decreaseLevel(int toDecrease) {
                        module.setLevel(module.getLevel() - toDecrease);
                    }

                }), ModuleHolder.CAPABILITY));
        }
    }

}
