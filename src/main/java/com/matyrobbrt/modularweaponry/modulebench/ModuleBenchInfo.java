package com.matyrobbrt.modularweaponry.modulebench;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.module.Module;
import com.matyrobbrt.modularweaponry.init.ModuleInit;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

//@formatter:off
public enum ModuleBenchInfo {

    INCOMPATIBLE_WITH_MODULE {

        @Override
        public Component makeComponent(Object... args) {
            return Translations.INCOMPATIBLE_WITH_MODULE.make(
                ((Module) args[0]).getName().copy().withStyle(ChatFormatting.GOLD)
            );
        }

        @Override
        public void encodeArgs(FriendlyByteBuf buffer, Object... args) {
            encodeModule(buffer, (Module) args[0]);
        }

        @Override
        public Object[] decodeArgs(FriendlyByteBuf buffer) {
            return new Object[] {decodeModule(buffer)};
        }
    },
    
    MAX_LEVEL_REACHED {

        @Override
        public Component makeComponent(Object... args) {
            return Translations.MAX_LEVEL_REACHED.make(
                ((Module) args[0]).getName().copy().withStyle(ChatFormatting.GOLD),
                args[1]
            );
        }

        @Override
        public void encodeArgs(FriendlyByteBuf buffer, Object... args) {
            encodeModule(buffer, (Module) args[0]);
            buffer.writeInt((int) args[1]);
        }

        @Override
        public Object[] decodeArgs(FriendlyByteBuf buffer) {
            return new Object[] {
                decodeModule(buffer),
                buffer.readInt()
            };
        }
        
    },
    
    NONE {

        @Override
        public Component makeComponent(Object... args) {
            return TextComponent.EMPTY;
        }

        @Override
        public void encodeArgs(FriendlyByteBuf buffer, Object... args) {
        }

        @Override
        public Object[] decodeArgs(FriendlyByteBuf buffer) {
            return new Object[0];
        }
        
    };
    
    public abstract Component makeComponent(Object... args);
    public abstract void encodeArgs(FriendlyByteBuf buffer, Object... args);
    public abstract Object[] decodeArgs(FriendlyByteBuf buffer);
    
    private static Module decodeModule(FriendlyByteBuf buffer) {
        return ModuleInit.REGISTRY.get().getValue(buffer.readResourceLocation());
    }
    
    private static void encodeModule(FriendlyByteBuf buf, Module module) {
        buf.writeResourceLocation(module.getRegistryName());
    }
    
    public enum Translations {
        INCOMPATIBLE_WITH_MODULE("incompatible_with_module"),
        MAX_LEVEL_REACHED("max_level_reached");
        
        private final String key;
        Translations(String key) {
            this.key = "info." + ModularWeaponry.MOD_ID + "." + key;
        }
        public TranslatableComponent make(Object... args) {
            return new TranslatableComponent(key, args);
        }
    }
}
