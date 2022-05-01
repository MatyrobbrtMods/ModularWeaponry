package com.matyrobbrt.modularweaponry.modulebench;

import com.matyrobbrt.modularweaponry.ModularWeaponry;
import com.matyrobbrt.modularweaponry.api.client.screen.InfoPanelWidget;
import com.matyrobbrt.modularweaponry.api.client.screen.Renderable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class ModuleBenchScreen extends AbstractContainerScreen<ModuleBenchContainer> implements ContainerListener {

    private static Component component;

    public static void updateComponent(Component component) {
        ModuleBenchScreen.component = component == TextComponent.EMPTY ? null : component;
    }

    public static final ResourceLocation TEXTURE_LOCATION = ModularWeaponry.rl("textures/gui/module_bench.png");

    private InfoPanelWidget panel;

    public ModuleBenchScreen(ModuleBenchContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        RenderSystem.disableBlend();
        this.renderFg(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    protected void renderFg(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    }

    // TODO render cost
    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pX, int pY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(pPoseStack, i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
        if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
            this.blit(pPoseStack, i + 99, j + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    protected void init() {
        super.init();
        menu.addSlotListener(this);

        this.panel = new InfoPanelWidget(330, 70, 126, 83,
            () -> component != null);

        if (panel.getRenderables().isEmpty()) {
            panel.getRenderables().add(new Renderable() {

                @Override
                public void render(int x, int y, PoseStack poseStack) {
                    if (component != null) {
                        Renderable.wordWrappedComponent(component, 100, 0xffffff, 1f).render(x, y + 12, poseStack);
                    }
                }

                @Override
                public int getWidth() {
                    return 0;
                }

                @Override
                @SuppressWarnings("resource")
                public int getHeight() {
                    return Minecraft.getInstance().font.lineHeight;
                }
            });
        }

        if (!renderables.contains(panel)) {
            addRenderableWidget(panel);
        }
    }

    @Override
    public void onClose() {
        menu.removeSlotListener(this);
        super.onClose();
    }

    @Override
    public void slotChanged(AbstractContainerMenu pContainerToSend, int pSlotInd, ItemStack pStack) {
    }

    @Override
    public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
    }

}
