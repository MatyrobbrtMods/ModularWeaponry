package com.matyrobbrt.modularweaponry.modulebench;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.cap.ModularTool;
import com.matyrobbrt.modularweaponry.api.cap.ModularTool.ActionType;
import com.matyrobbrt.modularweaponry.api.cap.ModuleHolder;
import com.matyrobbrt.modularweaponry.api.module.ModuleStack;
import com.matyrobbrt.modularweaponry.init.BlockInit;
import com.matyrobbrt.modularweaponry.init.MenuTypeInit;
import com.matyrobbrt.modularweaponry.network.packet.UpdateModuleBenchPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class ModuleBenchContainer extends AbstractContainerMenu {

    protected final Player player;
    protected final ContainerLevelAccess access;

    public static final int INPUT_SLOT = 0;
    public static final int MODULE_SLOT = 1;
    public static final int RESULT_SLOT = 2;

    protected final ResultContainer resultSlots = new ResultContainer();
    protected final Container inputSlots = new SimpleContainer(2) {

        @Override
        public void setChanged() {
            ModuleBenchContainer.this.slotsChanged(this);
        };
    };

    public final DataSlot cost = DataSlot.standalone();

    private final ModuleBenchBE tile;

    public ModuleBenchContainer(int pContainerId, Inventory inventory, ModuleBenchBE tile,
        ContainerLevelAccess access) {
        super(MenuTypeInit.MODULE_BENCH.get(), pContainerId);
        this.tile = tile;
        this.player = inventory.player;
        this.access = access;

        addSlot(new ModularTool.Slot(inputSlots, INPUT_SLOT, 27, 30));
        addSlot(new ModuleHolder.Slot(inputSlots, MODULE_SLOT, 76, 30));
        addSlot(new Slot(resultSlots, RESULT_SLOT, 134, 30) {

            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return ModuleBenchContainer.this.mayPickup(player, this.hasItem());
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                ModuleBenchContainer.this.onTake(player, stack);
            }
        });

        addDataSlot(cost);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }
    }

    public ModuleBenchContainer(int pContainerId, Inventory inventory, FriendlyByteBuf buffer) {
        this(pContainerId, inventory,
            DistExecutor.unsafeCallWhenOn(Dist.CLIENT,
                () -> () -> (ModuleBenchBE) Minecraft.getInstance().level.getBlockEntity(buffer.readBlockPos())),
            ContainerLevelAccess.NULL);
    }

    protected boolean mayPickup(Player player, boolean hasItem) {
        return (player.getAbilities().instabuild || player.experienceLevel >= this.cost.get()) && cost.get() > 0;
    }

    protected void onTake(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }

        final var moduleStack = inputSlots.getItem(MODULE_SLOT);
        final var module = moduleStack.getCapability(ModuleHolder.CAPABILITY).orElseThrow(RuntimeException::new);

        float consumedChance = module.getConsumedProbability();

        final var toolStack = inputSlots.getItem(INPUT_SLOT);
        final var tool = toolStack.getCapability(ModularTool.CAPABILITY).orElseThrow(RuntimeException::new);

        inputSlots.setItem(INPUT_SLOT, ItemStack.EMPTY);
        final var remainder = tool.addModule(module.getModule(), ActionType.EXECUTE);
        if (remainder.isEmpty()) {
            if (player.level.random.nextFloat() < consumedChance) {
                inputSlots.setItem(MODULE_SLOT, ItemStack.EMPTY);
            } else {
                module.decreaseLevel(module.getModule().getLevel());
            }
        } else {
            module.decreaseLevel(module.getModule().getLevel() - remainder.getLevel());
        }

        inputSlots.setItem(INPUT_SLOT, ItemStack.EMPTY);
        cost.set(0);
    }

    // TODO translation
    public void createResult() {
        updateMessage(ModuleBenchInfo.NONE);

        final var toolStack = inputSlots.getItem(INPUT_SLOT);
        if (toolStack.isEmpty())
            return;
        final var tool = toolStack.getCapability(ModularTool.CAPABILITY).orElseThrow(RuntimeException::new);

        final var moduleItem = inputSlots.getItem(MODULE_SLOT);
        if (moduleItem.isEmpty())
            return;
        final var moduleHolder = moduleItem.getCapability(ModuleHolder.CAPABILITY).orElseThrow(RuntimeException::new);
        final var moduleStack = moduleHolder.getModule();

        if (!tool.getType().isCompatibleWithModule(moduleStack.getModule())
            || !moduleStack.getModule().isCompatibleWithTool(tool.getType())) {
            updateMessage(ModuleBenchInfo.INCOMPATIBLE_WITH_MODULE, moduleStack.getModule());
            return;
        }

        //@formatter:off
        final var mutualities = tool.getModules()
            .stream()
            .filter(s -> !s.isCompatibleWith(moduleStack))
            .toList();
        if (!mutualities.isEmpty()) {
            // Has mutually incompatible modules, can't install.
//            updateMessage(new TextComponent("The module ")
//                .append(moduleStack.getName())
//                .append(" can't be installed on a tool that has ")
//                .append(mutualities.get(0).getName())
//                .append(" as the two modules are mutually exclusive.")
//            );
            return;
        }
        // Nothing is mutually exclusive, everything is compatible
        // let's check for max levels
        final var alreadyExisting = tool.getModules()
            .stream()
            .filter(s -> s.getModule() == moduleStack.getModule())
            .findFirst();
        if (alreadyExisting.isPresent()) {
            final var max = moduleStack.getModule().getMaxLevel();
            final var alreadyLevel = alreadyExisting.get().getLevel();
            if (max <= alreadyLevel) {
                updateMessage(ModuleBenchInfo.MAX_LEVEL_REACHED, moduleStack.getModule(), max);
                return;
            }
        }
        
        // Next: let's see if weight is reached
        final var weightAlready = tool.getModules().stream().mapToInt(ModuleStack::getWeight).sum();
        final var allowedLevel = Math.min(moduleStack.getLevel(),
            (tool.getMaximumModuleWeight() - weightAlready) / moduleStack.getModule().getLevelWeight());
        
        if (allowedLevel < 1) {
//            updateMessage(new TextComponent("This module cannot be added as the tool would hit its maximum module weight of " + tool.getMaximumModuleWeight()));
            return;
        }
        
        // Right so, all checks passed, let's copy the tool
        final var resultToolStack = toolStack.copy();
        // Now let's add modules to it
        resultToolStack.getCapability(ModularTool.CAPABILITY)
            .orElseThrow(RuntimeException::new)
            .addModule(moduleStack, ActionType.EXECUTE);
        // And now we can put it in the result slot
        resultSlots.setItem(0, resultToolStack);
        
        cost.set(12);
        
        broadcastChanges();
    }
    
    private void updateMessage(ModuleBenchInfo info, Object... args) {
        if (player.level.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ModuleBenchScreen.updateComponent(info.makeComponent(args)));
        } else if (player instanceof ServerPlayer serverPlayer) {
            ModularWeaponry.packetHandler.sendTo(new UpdateModuleBenchPacket(info, args), serverPlayer);
        }
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39796_, p_39797_) -> {
           this.clearContainer(pPlayer, this.inputSlots);
        });
    }

    /**
     * Determines whether supplied player can use this container
     */
    @Override
    public boolean stillValid(Player pPlayer) {
        return this.access.evaluate((level, pos) -> {
            return level.getBlockState(pos).getBlock() != BlockInit.MODULE_BENCH.get() ? false
                : pPlayer.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }, true);
    }

    @Override
    public void slotsChanged(Container pInventory) {
        super.slotsChanged(pInventory);
        if (pInventory == this.inputSlots) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.createResult();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        final var slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true))
                    return ItemStack.EMPTY;

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex != 0 && pIndex != 1) {
                if (pIndex >= 3 && pIndex < 39) {
                    if (!this.moveItemStackTo(itemstack1, MODULE_SLOT, RESULT_SLOT, false))
                        return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false))
                return ItemStack.EMPTY;

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    public ModuleBenchBE getTile() {
        return tile;
    }

}
