package com.matyrobbrt.modularweaponry;

import com.matyrobbrt.modularweaponry.init.MenuTypeInit;
import com.matyrobbrt.modularweaponry.modulebench.ModuleBenchScreen;

import net.minecraft.client.gui.screens.MenuScreens;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class MDClientSetup {

    @SubscribeEvent
    static void setupClient(final FMLClientSetupEvent event) {
        MenuScreens.register(MenuTypeInit.MODULE_BENCH.get(), ModuleBenchScreen::new);
    }

}
