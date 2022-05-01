package com.matyrobbrt.modularweaponry.network.packet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.cap.ModularTool;
import com.matyrobbrt.modularweaponry.api.module.Module;
import com.matyrobbrt.modularweaponry.api.network.Packet;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkEvent.Context;

public class ChangeActiveModulePacket implements Packet {

    private final EquipmentSlot.Type slotType;
    private final byte slotId;
    @Nullable
    private final Integer selectedModuleIndex;

    public ChangeActiveModulePacket(Type slotType, byte slotId, @Nullable Integer selectedModuleIndex) {
        this.slotType = slotType;
        this.slotId = slotId;
        this.selectedModuleIndex = selectedModuleIndex;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(slotType);
        buffer.writeByte(slotId);

        buffer.writeBoolean(selectedModuleIndex == null);
        if (selectedModuleIndex != null) {
            buffer.writeInt(selectedModuleIndex);
        }
    }

    public static ChangeActiveModulePacket decode(FriendlyByteBuf buffer) {
        final var sType = buffer.readEnum(EquipmentSlot.Type.class);
        final var slotId = buffer.readByte();
        final var selectedModuleIndex = buffer.readBoolean() ? null : buffer.readInt();
        return new ChangeActiveModulePacket(sType, slotId, selectedModuleIndex);
    }

    @Override
    public void handle(Context context) {
        final var player = context.getSender();
        if (slotType == Type.ARMOR) {
            final var stack = player.getInventory().getArmor(slotId);
            stack.getCapability(ModularTool.CAPABILITY).ifPresent(t -> changeActiveModule(stack, t, context));
        } else {
            final var stack = slotId == 0 ? player.getMainHandItem() : player.getOffhandItem();
            stack.getCapability(ModularTool.CAPABILITY).ifPresent(t -> changeActiveModule(stack, t, context));
        }
    }

    private void changeActiveModule(final ItemStack stack, final ModularTool tool, Context context) {
        final var modules = resolveActiveModules(tool);
        if (modules.isEmpty())
            return;
        final var nextIndex = selectedModuleIndex == null ? resolveNextIndex(modules, tool.getSelectedActiveModule())
            : selectedModuleIndex;
        if (nextIndex == ModularTool.NO_ACTIVE_MODULE_SELECTED)
            return;
        final var nextModule = tool.getModules().get(nextIndex);
        tool.setSelectedActiveModule(nextIndex);
        context.getSender().sendMessage(
            new TextComponent("[ModularWeaponry] ").append(ModularWeaponry.component("message",
                "changed_active_modules", nextModule.getModule().getName().copy().withStyle(ChatFormatting.GOLD))),
            Util.NIL_UUID);
    }

    private static int resolveNextIndex(final List<Pair<Integer, Module>> modules, final int currentlySelected) {
        if (currentlySelected == ModularTool.NO_ACTIVE_MODULE_SELECTED) { return modules.get(0).getKey(); }
        final var current = modules.stream().filter(p -> p.getKey() == currentlySelected).findFirst();
        if (current.isPresent()) {
            final var currentIndex = modules.indexOf(current.get());
            if (modules.size() == currentIndex - 1) {
                return 0;
            } else {
                return currentIndex + 1;
            }
        } else {
            return ModularTool.NO_ACTIVE_MODULE_SELECTED;
        }
    }

    private static List<Pair<Integer, Module>> resolveActiveModules(final ModularTool tool) {
        final List<Pair<Integer, Module>> modules = new ArrayList<>();
        final var aM = tool.getModules();
        for (var i = 0; i < aM.size(); i++) {
            final var module = aM.get(i);
            if (module.getModule().isActive()) {
                modules.add(Pair.of(i, module.getModule()));
            }
        }
        return modules;
    }
}
