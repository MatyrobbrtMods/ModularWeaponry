package com.matyrobbrt.modularweaponry.event;

import com.matyrobbrt.modularweaponry.api.cap.ModularTool;

import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ModuleHandlingEvents {

    @SubscribeEvent
    static void onPlayerAttack(final AttackEntityEvent event) {
        event.getPlayer().getMainHandItem().getCapability(ModularTool.CAPABILITY).ifPresent(tool -> tool.getModules()
            .forEach(md -> md.onPlayerAttackTarget(event.getPlayer(), event.getEntity())));
    }

}
