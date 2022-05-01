package com.matyrobbrt.modularweaponry.api.client.key;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.matyrobbrt.modularweaponry.api.client.key.MDKeyBinding.KeyDownHandler;
import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KeyBindingBuilder {

    private final Consumer<KeyMapping> whenBuilt;
    private String description;
    private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;
    private KeyModifier keyModifier = KeyModifier.NONE;
    private InputConstants.Key key;
    private String category = "ModularWeaponry";
    private KeyDownHandler onKeyDown;
    private Consumer<KeyMapping> onKeyUp;
    private BooleanSupplier toggleable;
    private boolean repeating;

    public KeyBindingBuilder(final Consumer<KeyMapping> whenBuilt) {
        this.whenBuilt = whenBuilt;
    }

    public KeyBindingBuilder description(String description) {
        this.description = Objects.requireNonNull(description, "Description cannot be null.");
        return this;
    }

    public KeyBindingBuilder conflictInGame() {
        return conflictContext(KeyConflictContext.IN_GAME);
    }

    public KeyBindingBuilder conflictInGui() {
        return conflictContext(KeyConflictContext.GUI);
    }

    public KeyBindingBuilder conflictContext(IKeyConflictContext keyConflictContext) {
        this.keyConflictContext = Objects.requireNonNull(keyConflictContext, "Key conflict context cannot be null.");
        return this;
    }

    public KeyBindingBuilder modifier(KeyModifier keyModifier) {
        this.keyModifier = Objects.requireNonNull(keyModifier, "Key modifier cannot be null.");
        return this;
    }

    public KeyBindingBuilder keyCode(int keyCode) {
        return keyCode(InputConstants.Type.KEYSYM, keyCode);
    }

    public KeyBindingBuilder keyCode(InputConstants.Type keyType, int keyCode) {
        Objects.requireNonNull(keyType, "Key type cannot be null.");
        return keyCode(keyType.getOrCreate(keyCode));
    }

    public KeyBindingBuilder keyCode(InputConstants.Key key) {
        this.key = Objects.requireNonNull(key, "Key cannot be null.");
        return this;
    }

    public KeyBindingBuilder category(String category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null.");
        return this;
    }

    public KeyBindingBuilder onKeyDown(KeyDownHandler onKeyDown) {
        this.onKeyDown = Objects.requireNonNull(onKeyDown, "On key down cannot be null when manually specified.");
        return this;
    }

    public KeyBindingBuilder onKeyUp(Consumer<KeyMapping> onKeyUp) {
        this.onKeyUp = Objects.requireNonNull(onKeyUp, "On key up cannot be null when manually specified.");
        return this;
    }

    public KeyBindingBuilder toggleable() {
        return toggleable(() -> true);
    }

    public KeyBindingBuilder toggleable(BooleanSupplier toggleable) {
        this.toggleable = Objects.requireNonNull(toggleable,
            "Toggleable supplier cannot be null when manually specified.");
        return this;
    }

    public KeyBindingBuilder repeating() {
        this.repeating = true;
        return this;
    }

    public KeyMapping build() {
        final var k = new MDKeyBinding(Objects.requireNonNull(description, "Description has not been set."),
            keyConflictContext, keyModifier, Objects.requireNonNull(key, "Key has not been set"), category, onKeyDown,
            onKeyUp, toggleable, repeating);
        if (whenBuilt != null)
            whenBuilt.accept(k);
        return k;
    }
}
