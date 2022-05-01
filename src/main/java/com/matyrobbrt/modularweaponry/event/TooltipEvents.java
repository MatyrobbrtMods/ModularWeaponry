package com.matyrobbrt.modularweaponry.event;

import java.util.function.Consumer;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.cap.ModularTool;
import com.matyrobbrt.modularweaponry.api.cap.ModuleHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TooltipEvents {

    @SubscribeEvent
    static void onTooltipEvent(final ItemTooltipEvent event) {
        event.getItemStack().getCapability(ModuleHolder.CAPABILITY)
            .ifPresent(h -> moduleHolder(h, event.getToolTip()::add));
        event.getItemStack().getCapability(ModularTool.CAPABILITY)
            .ifPresent(h -> modularTool(h, event.getToolTip()::add));
    }

    public static void moduleHolder(final ModuleHolder holder, Consumer<? super Component> consumer) {
        final var module = holder.getModule();
        consumer.accept(ModularWeaponry
            .tooltip("module_holder", module.getModule().getName().copy().withStyle(ChatFormatting.GREEN),
                new TextComponent(String.valueOf(module.getLevel())).withStyle(ChatFormatting.GOLD))
            .withStyle(ChatFormatting.GRAY));
    }

    public static void modularTool(final ModularTool tool, Consumer<? super Component> consumer) {
        final var modules = tool.getModules();
        if (modules.isEmpty()) {
            consumer.accept(ModularWeaponry.tooltip("modular_tool.empty").withStyle(ChatFormatting.GRAY));
            return;
        }
        //@formatter:off
        consumer.accept(ModularWeaponry.tooltip("modular_tool.contains").withStyle(ChatFormatting.GRAY));
        for (final var module : modules) {
            if (module.getLevel() < 1) return;
            consumer.accept(
                new TextComponent("   \u2022 ")
                    .append(module.getModule().getName().copy().withStyle(ChatFormatting.GOLD))
                    .append(": ")
                    .append(String.valueOf(module.getLevel()))
            );
        }
    }

}
