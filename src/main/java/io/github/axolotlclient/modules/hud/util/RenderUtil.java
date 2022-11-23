/*
 * Copyright © 2021-2022 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.hud.util;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.mixin.MinecraftClientAccessor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.function.Supplier;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

@UtilityClass
public class RenderUtil {

    /**
     * Fills an outline with x/y width/height values
     */
    public void drawOutline(int x, int y, int width, int height, int color) {
        fillOutline(x, y, x + width, y + height, color);
    }

    public void drawOutline(int x, int y, int width, int height, Color color) {
        fillOutline(x, y, x + width, y + height, color);
    }

    public void fillOutline(int x, int y, int x2, int y2, Color color) {
        // Top line
        fill(x, y, x2, y + 1, color);
        // Left line
        fill(x, y + 1, x + 1, y2 - 1, color);
        // Right line
        fill(x2 - 1, y + 1, x2, y2 - 1, color);
        // Bottom line
        fill(x, y2 - 1, x2, y2, color);
    }

    /**
     * Draws an outline with raw x/y values
     */
    public void fillOutline(int x, int y, int x2, int y2, int color) {
        // Top line
        fill(x, y, x2, y + 1, color);
        // Left line
        fill(x, y + 1, x + 1, y2 - 1, color);
        // Right line
        fill(x2 - 1, y + 1, x2, y2 - 1, color);
        // Bottom line
        fill(x, y2 - 1, x2, y2, color);
    }

    /**
     * Draws a vertical line
     */
    public void drawVerticalLine(int x, int y, int height, int color) {
        drawRectangle(x, y, 1, height, color);
    }

    /**
     * Draws a horizontal line
     */
    public void drawHorizontalLine(int x, int y, int width, int color) {
        drawRectangle(x, y, width, 1, color);
    }

    /**
     * Fills in a rectangle with a color. x/y width/height
     */
    public void drawRectangle(int x, int y, int width, int height, int color) {
        fill(x, y, x + width, y + height, color);
    }

    /**
     * Fills in a rectangle with a color. Uses raw x/y values. x/y
     */
    public void fill(int x1, int y1, int x2, int y2, int color) {
        fill(x1, y1, x2, y2, color, () -> MinecraftClient.getInstance().gameRenderer.getShader());
    }

    public void drawRectangle(int x, int y, int width, int height, Color color) {
        fill(x, y, x + width, y + height, color);
    }

    public void fillRect(int x, int y, int width, int height, int color, boolean keepBlend) {
        if (!keepBlend) {
            drawRectangle(x, y, width, height, color);
        } else {
            fillBlend(x, y, x + width, y + height, color);
        }
    }

    public void fillBlend(int x, int y, int width, int height, Color color) {
        fillBlend(x, y, x + width, y + height, color.getAsInt());
    }

    public void fillBlend(int x1, int y1, int x2, int y2, int color) {
        GlStateManager.disableTexture();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        float a = (float) (color >> 24 & 0xFF) / 255.0f;
        float r = (float) (color >> 16 & 0xFF) / 255.0f;
        float g = (float) (color >> 8 & 0xFF) / 255.0f;
        float b = (float) (color & 0xFF) / 255.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x1, y2, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x2, y2, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x2, y1, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x1, y1, 0.0f).color(r, g, b, a).next();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture();
    }

    public void fill(int x1, int y1, int x2, int y2, Color color) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        int colorInt = colorPreRender(color);
        float a = (float) (colorInt >> 24 & 0xFF) / 255.0f;
        float r = (float) (colorInt >> 16 & 0xFF) / 255.0f;
        float g = (float) (colorInt >> 8 & 0xFF) / 255.0f;
        float b = (float) (colorInt & 0xFF) / 255.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x1, y2, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x2, y2, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x2, y1, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x1, y1, 0.0f).color(r, g, b, a).next();

        Tessellator.getInstance().draw();
        colorPostRender(color);
    }

    public void fill(int x1, int y1, int x2, int y2, int color, Supplier<ShaderEffect> shaderSupplier) {
        int i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }
        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }
        float a = (float) (color >> 24 & 0xFF) / 255.0f;
        float r = (float) (color >> 16 & 0xFF) / 255.0f;
        float g = (float) (color >> 8 & 0xFF) / 255.0f;
        float b = (float) (color & 0xFF) / 255.0f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        //GlStateManager.setShader(shaderSupplier);
        if (shaderSupplier.get() != null) {
            shaderSupplier.get()
                    .render(((MinecraftClientAccessor) MinecraftClient.getInstance()).getTicker().tickDelta);
        }
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x1, y2, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x2, y2, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x2, y1, 0.0f).color(r, g, b, a).next();
        bufferBuilder.vertex(x1, y1, 0.0f).color(r, g, b, a).next();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public int colorPreRender(Color color) {
        GlStateManager.color4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f,
                color.getAlpha() / 255f);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        //RenderSystem.setShader(getShader());

        return color.getAsInt();
    }

    public void colorPostRender(Color color) {
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.color4f(1, 1, 1, 1);
    }
}
