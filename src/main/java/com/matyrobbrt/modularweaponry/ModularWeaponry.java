package com.matyrobbrt.modularweaponry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matyrobbrt.modularweaponry.api.cap.ModularTool;
import com.matyrobbrt.modularweaponry.api.cap.ModuleHolder;
import com.matyrobbrt.modularweaponry.api.network.BasePacketHandler;
import com.matyrobbrt.modularweaponry.api.utils.Version;
import com.matyrobbrt.modularweaponry.init.BETypeInit;
import com.matyrobbrt.modularweaponry.init.BlockInit;
import com.matyrobbrt.modularweaponry.init.ItemInit;
import com.matyrobbrt.modularweaponry.init.KeyBindingInit;
import com.matyrobbrt.modularweaponry.init.MenuTypeInit;
import com.matyrobbrt.modularweaponry.init.ModuleInit;
import com.matyrobbrt.modularweaponry.init.ToolTypeInit;
import com.matyrobbrt.modularweaponry.network.PacketHandler;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModularWeaponry.MOD_ID)
@SuppressWarnings("static-method")
public class ModularWeaponry {

    public static final String MOD_ID = "modularweaponry";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final CreativeModeTab TAB = new CreativeModeTab(CreativeModeTab.getGroupCountSafe(), MOD_ID) {

        @Override
        public ItemStack makeIcon() {
            // TODO add an actual icon
            return ItemStack.EMPTY;
        }
    };

    public static PacketHandler packetHandler;

    public ModularWeaponry() {
        final var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        packetHandler = new PacketHandler(
            BasePacketHandler.createChannel(rl("network"), new Version(ModLoadingContext.get().getActiveContainer())));

        BlockInit.BLOCKS.register(modBus);
        BETypeInit.BE_TYPES.register(modBus);
        ItemInit.ITEMS.register(modBus);
        ToolTypeInit.TOOL_TYPES.register(modBus);
        ModuleInit.MODULES.register(modBus);
        MenuTypeInit.MENUS.register(modBus);

        modBus.register(this);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modBus.register(MDClientSetup.class);
            modBus.register(KeyBindingInit.class);
        });
    }

    @SubscribeEvent
    void registerCaps(final RegisterCapabilitiesEvent event) {
        event.register(ModularTool.class);
        event.register(ModuleHolder.class);
    }

    @SubscribeEvent
    void commonSetup(final FMLCommonSetupEvent event) {
        packetHandler.initialize();
    }

    public static final ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static TranslatableComponent tooltip(String key, Object... args) {
        return new TranslatableComponent("tooltip." + MOD_ID + "." + key, args);
    }

    public static TranslatableComponent component(String key, String path, Object... args) {
        return new TranslatableComponent(key + "." + MOD_ID + "." + path, args);
    }
}
