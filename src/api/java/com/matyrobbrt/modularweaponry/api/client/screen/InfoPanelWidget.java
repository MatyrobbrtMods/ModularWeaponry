package com.matyrobbrt.modularweaponry.api.client.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class InfoPanelWidget extends AbstractWidget implements Widget, GuiEventListener {

    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("modularweaponry",
        "textures/gui/panel.png");

    private final List<Renderable> renderables = new ArrayList<>();
    private final BooleanSupplier isVisible;

    public InfoPanelWidget(int pX, int pY, int pWidth, int pHeight, BooleanSupplier isVisible) {
        super(pX, pY, pWidth, pHeight, TextComponent.EMPTY);
        this.isVisible = isVisible;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (!isVisible.getAsBoolean())
            return;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        blit(pPoseStack, x, y, 126, 0, 126, 83);
        final int centreX = width / 2 + x;
        final int space = 4;
        int y = this.y + 7;
        for (final var renderable : renderables) {
            renderable.render(centreX, y, pPoseStack);
            y += renderable.getHeight() + space;
        }
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
    }

    public List<Renderable> getRenderables() {
        return renderables;
    }
}
