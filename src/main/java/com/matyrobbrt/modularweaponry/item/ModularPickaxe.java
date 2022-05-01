package com.matyrobbrt.modularweaponry.item;

import java.util.List;
import java.util.Optional;

import com.matyrobbrt.modularweaponry.api.cap.ModularTool;
import com.matyrobbrt.modularweaponry.api.module.ModuleStack;
import com.matyrobbrt.modularweaponry.cap.OneCapabilityProvider;
import com.matyrobbrt.modularweaponry.init.ModuleInit;
import com.matyrobbrt.modularweaponry.init.ToolTypeInit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class ModularPickaxe extends PickaxeItem {

    public ModularPickaxe(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        stack.getCapability(ModularTool.CAPABILITY)
            .ifPresent(t -> t.addModule(ModuleInit.TEST.get().createStack(1), ModularTool.ActionType.EXECUTE));
        return super.onLeftClickEntity(stack, player, entity);
    }

    public static Optional<List<ModuleStack>> getModules(ItemStack stack) {
        return stack.getCapability(ModularTool.CAPABILITY).map(ModularTool::getModules);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new OneCapabilityProvider<>(
            LazyOptional.of(() -> new ModularTool.Implementation(ToolTypeInit.PICKAXE.get(), 1)),
            ModularTool.CAPABILITY);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }

}
