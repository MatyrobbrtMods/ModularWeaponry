package com.matyrobbrt.modularweaponry.module;

import com.matyrobbrt.modularweaponry.api.ToolType;
import com.matyrobbrt.modularweaponry.api.module.Module;
import com.matyrobbrt.modularweaponry.api.module.ModuleStack;
import com.matyrobbrt.modularweaponry.init.ToolTypeInit;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;

public class LightningAttackModule extends Module {

    @Override
    public void onPlayerAttackTarget(Player player, Entity target, ModuleStack module) {
        for (int i = 1; i <= module.getLevel(); i++) {
            final var l = new LightningBolt(EntityType.LIGHTNING_BOLT, target.level);
            l.setPos(target.position());
            target.level.addFreshEntity(l);
        }
    }

    @Override
    public boolean isCompatibleWithTool(ToolType toolType) {
        return toolType == ToolTypeInit.SWORD.get();
    }

}
