package com.matyrobbrt.modularweaponry.api.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.common.util.Lazy;

public final class ModuleStack {

    //@formatter:off
    public static final Codec<ModuleStack> CODEC = RecordCodecBuilder.create(in -> in.group(
	    Module.REGISTRY.get().getCodec().fieldOf("module").forGetter(ModuleStack::getModule),
		Codec.INT.fieldOf("level").forGetter(ModuleStack::getLevel),
		Codec.BOOL.optionalFieldOf("immutable", false).forGetter(s -> s.immutable)
	).apply(in, ModuleStack::new));
	//@formatter:on

    public static final Lazy<ModuleStack> EMPTY = Lazy.concurrentOf(() -> new ModuleStack(Module.EMPTY.get()));

    private final Module module;
    private final boolean immutable;
    private int level;

    public ModuleStack(final Module module) {
        this(module, 1);
    }

    public ModuleStack(final Module module, final int level) {
        this(module, level, false);
    }

    public ModuleStack(final Module module, final int level, final boolean immutable) {
        this.module = module;
        this.level = Math.min(module.getMaxLevel(), level);
        this.immutable = immutable;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        if (!immutable) {
            this.level = Math.min(module.getMaxLevel(), level);
        }
    }

    /**
     * Creates a copy of this stack.
     * 
     * @return a copy of this stack
     */
    public ModuleStack copy() {
        return new ModuleStack(module, level, immutable);
    }

    /**
     * Creates an immutable copy of this stack.
     * 
     * @return an immutable copy of this stack
     */
    public ModuleStack immutable() {
        return new ModuleStack(module, level, true);
    }

    public Module getModule() {
        return module;
    }

    public boolean isEmpty() {
        return module == Module.EMPTY.get() || level <= 0;
    }

    public Component getName() {
        return module.getName();
    }

    public boolean isCompatibleWith(Module other) {
        return module.isCompatibleWith(other);
    }

    public boolean isCompatibleWith(ModuleStack other) {
        return module.isCompatibleWith(other.getModule());
    }

    public int getWeight() {
        return level * module.getLevelWeight();
    }

    public void onPlayerAttackTarget(final Player player, final Entity target) {
        if (level > 0) {
            module.onPlayerAttackTarget(player, target, this);
        }
    }

    @Override
    public int hashCode() {
        return level * 12 + module.hashCode() * 5;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ModuleStack stack))
            return false;
        return stack.getLevel() == level && stack.getModule() == module && stack.immutable == this.immutable;
    }

    public Tag serialize() {
        return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(false, e -> {});
    }

    public static ModuleStack deserialize(Tag tag) {
        return CODEC.decode(NbtOps.INSTANCE, tag).getOrThrow(false, e -> {}).getFirst();
    }
}
