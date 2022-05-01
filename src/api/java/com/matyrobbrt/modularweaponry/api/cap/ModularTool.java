package com.matyrobbrt.modularweaponry.api.cap;

import java.util.ArrayList;
import java.util.List;

import com.matyrobbrt.modularweaponry.api.ToolType;
import com.matyrobbrt.modularweaponry.api.module.ModuleStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

// TODO add modules max weigh
public interface ModularTool {

    Capability<ModularTool> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    int NO_ACTIVE_MODULE_SELECTED = -1;

    /**
     * Returns an <b>immutable</b> view of the modules installed.
     * 
     * @return an <b>immutable</b> view of the modules installed
     */
    List<ModuleStack> getModules();

    ModuleStack addModule(ModuleStack module, ActionType action);

    ToolType getType();

    int getSelectedActiveModule();

    void setSelectedActiveModule(int slotId);

    int getMaximumModuleWeight();

    public enum ActionType {
        SIMULATE, EXECUTE
    }

    class Implementation implements ModularTool, INBTSerializable<CompoundTag> {

        private final List<ModuleStack> modules = new ArrayList<>();
        private final ToolType type;
        private final int maxWeight;

        private int selectedActiveModule = NO_ACTIVE_MODULE_SELECTED;

        public Implementation(ToolType type, int maxWeight) {
            this.type = type;
            this.maxWeight = maxWeight;
        }

        @Override
        public CompoundTag serializeNBT() {
            final var nbt = new CompoundTag();
            final var modulesTag = new ListTag();
            modules.forEach(s -> modulesTag.add(s.serialize()));
            nbt.put("modules", modulesTag);
            nbt.putInt("selectedActiveModule", selectedActiveModule);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            modules.clear();
            final var modulesTag = nbt.getList("modules", net.minecraft.nbt.Tag.TAG_COMPOUND);
            modulesTag.forEach(tag -> modules.add(ModuleStack.deserialize(tag)));
            selectedActiveModule = nbt.getInt("selectedActiveModule");
        }

        @Override
        public ModuleStack addModule(ModuleStack module, ActionType action) {
            if (!(module.getModule().isCompatibleWithTool(type) && type.isCompatibleWithModule(module.getModule())))
                return module;
            final var weightAlready = modules.stream().mapToInt(ModuleStack::getWeight).sum();
            final var allowedLevel = Math.min(module.getLevel(),
                (getMaximumModuleWeight() - weightAlready) / module.getModule().getLevelWeight());
            final var alreadyExisting = modules.stream().filter(s -> s.getModule() == module.getModule()).findFirst();
            if (alreadyExisting.isPresent()) {
                final var alreadyLevel = alreadyExisting.get().getLevel();
                var add = allowedLevel;
                if ((allowedLevel + alreadyLevel) > module.getModule().getMaxLevel()) {
                    add = (allowedLevel + alreadyLevel) - module.getModule().getMaxLevel();
                }
                final var toAddLevels = Math.max(0, add);
                if (action == ActionType.EXECUTE) {
                    alreadyExisting.get().setLevel(alreadyLevel + toAddLevels);
                }
                return module.getModule().createStack(Math.max(module.getLevel() - toAddLevels, 0));
            } else {
                final var incompats = modules.stream().filter(s -> !s.getModule().isCompatibleWith(module.getModule()))
                    .findFirst();
                if (incompats.isPresent())
                    return module;
                else {
                    if (action == ActionType.EXECUTE) {
                        modules.add(module.getModule().createStack(allowedLevel));
                    }
                    return module.getModule().createStack(Math.max(module.getLevel() - allowedLevel, 0));
                }
            }
        }

        @Override
        public List<ModuleStack> getModules() {
            return List.copyOf(modules);
        }

        @Override
        public ToolType getType() {
            return type;
        }

        @Override
        public int getSelectedActiveModule() {
            return selectedActiveModule;
        }

        @Override
        public void setSelectedActiveModule(int slotId) {
            this.selectedActiveModule = slotId;
        }

        @Override
        public int getMaximumModuleWeight() {
            return maxWeight;
        }

    }

    /**
     * A slot that only allows module tools to be put in it.
     * 
     * @author matyrobbrt
     *
     */
    class Slot extends net.minecraft.world.inventory.Slot {

        public Slot(Container pContainer, int pIndex, int pX, int pY) {
            super(pContainer, pIndex, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            // Only allow module tools.
            return pStack.getCapability(CAPABILITY).isPresent();
        }

    }
}
