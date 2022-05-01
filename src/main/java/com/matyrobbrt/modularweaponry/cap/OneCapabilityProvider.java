package com.matyrobbrt.modularweaponry.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class OneCapabilityProvider<C> implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    private final LazyOptional<? extends C> value;
    private final Capability<C> cap;
    private final String name;

    public OneCapabilityProvider(LazyOptional<? extends C> value, Capability<C> cap, String name) {
        this.value = value;
        this.cap = cap;
        this.name = name;
    }

    public OneCapabilityProvider(LazyOptional<? extends C> value, Capability<C> cap) {
        this.value = value;
        this.cap = cap;
        this.name = cap.getName();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public CompoundTag serializeNBT() {
        final var tag = new CompoundTag();
        if (value.resolve().orElseThrow() instanceof INBTSerializable nS)
            tag.put(name, nS.serializeNBT());
        return tag;
    }

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (value.resolve().orElseThrow() instanceof INBTSerializable nS) {
            nS.deserializeNBT(nbt.get(name));
        }
    }

    @Override
    public <TZ> LazyOptional<TZ> getCapability(Capability<TZ> cap, Direction side) {
        if (cap == this.cap)
            return value.cast();
        return LazyOptional.empty();
    }

}
