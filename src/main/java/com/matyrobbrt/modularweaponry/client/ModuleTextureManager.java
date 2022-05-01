package com.matyrobbrt.modularweaponry.client;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.utils.CallerSensitiveOneTimeHolder;
import com.matyrobbrt.modularweaponry.init.ModuleInit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.CLIENT, modid = ModularWeaponry.MOD_ID)
public class ModuleTextureManager extends TextureAtlasHolder {

    public static final Supplier<ModuleTextureManager> INSTANCE = new CallerSensitiveOneTimeHolder<>(
        ModuleTextureManager.class);

    @SubscribeEvent
    static void onClientListeners(final RegisterClientReloadListenersEvent event) {
        ((CallerSensitiveOneTimeHolder<ModuleTextureManager>) INSTANCE)
            .accept(() -> new ModuleTextureManager(Minecraft.getInstance().getTextureManager()));
        event.registerReloadListener(INSTANCE.get());
    }

    private ModuleTextureManager(TextureManager pTextureManager) {
        super(pTextureManager, ModularWeaponry.rl("textures/atlas/modules.png"), ModularWeaponry.MOD_ID + "_modules");
    }

    @Override
    protected Stream<ResourceLocation> getResourcesToLoad() {
        return ModuleInit.REGISTRY.get().getKeys().stream();
    }

    public TextureAtlasSprite get(com.matyrobbrt.modularweaponry.api.module.Module module) {
        return this.getSprite(module.getRegistryName());
    }
}
