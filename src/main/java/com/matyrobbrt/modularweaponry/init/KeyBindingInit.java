package com.matyrobbrt.modularweaponry.init;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.client.key.KeyBindingBuilder;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.Util.makeDescriptionId;

import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

//@formatter:off
// TODO active modules still need to be made
public class KeyBindingInit {
    
    private static final List<KeyMapping> KEYS = new ArrayList<>();
    
    public static final KeyMapping OPEN_MENU_MODIFIER = key()
        .conflictInGame()
        .description(makeDescription("open_menu_modifier"))
        .keyCode(GLFW.GLFW_KEY_APOSTROPHE)
        .build();
    
    @SubscribeEvent
    static void onClientSetup(final FMLClientSetupEvent event) {
        KEYS.forEach(ClientRegistry::registerKeyBinding);
    }
    
    private static KeyBindingBuilder key() {
        return new KeyBindingBuilder(KEYS::add);
    }
    
    private static String makeDescription(String path) {
        return makeDescriptionId("key", ModularWeaponry.rl(path));
    }
    
    public static void handleActiveModule(final ItemStack stack) {
        // TODO
    }
}
