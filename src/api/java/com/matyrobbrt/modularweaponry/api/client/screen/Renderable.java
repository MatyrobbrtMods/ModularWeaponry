package com.matyrobbrt.modularweaponry.api.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

public interface Renderable {

    int getWidth();

    int getHeight();

    void render(int x, int y, PoseStack poseStack);

    @SuppressWarnings("resource")
    static Renderable wordWrappedComponent(Component component, int maxWidth, int colour, float scale) {
        return new Renderable() {

            @Override
            public void render(int x, int y, PoseStack poseStack) {
                final var times = 1 / scale;
                final var perLine = Minecraft.getInstance().font.lineHeight / times;
                poseStack.pushPose();
                x = (int) (x * times);
                y = (int) (y * times);
                poseStack.scale(scale, scale, scale);
                for (final var line : Minecraft.getInstance().font.split(component, maxWidth)) {
                    GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, line, x, y, colour);
                    y += perLine;
                }
                poseStack.popPose();
            }

            @Override
            public int getWidth() {
                return Minecraft.getInstance().font.width(component);
            }

            @Override
            public int getHeight() {
                return Minecraft.getInstance().font.lineHeight;
            }
        };
    }
}
