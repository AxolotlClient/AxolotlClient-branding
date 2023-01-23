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

package io.github.axolotlclient.modules.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.mixin.WorldRendererAccessor;
import io.github.axolotlclient.util.Logger;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.Locale;
import java.util.Objects;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * <a href="https://github.com/AMereBagatelle/FabricSkyBoxes">Github Link.</a>
 * @license MIT
 **/

// TODO fix rotation & blending issues with shooting stars, implement more missing features like worlds, weather, biomes...

public abstract class SkyboxInstance {

    JsonObject object;
    float alpha = 1F;
    Identifier[] textures = new Identifier[6];
    int[] fade = new int[4];

    protected int blendMode = 1;

    // ! These are the options variables.  Do not mess with these.
    protected boolean alwaysOn;
    protected float maxAlpha = 1f;
    protected boolean manualBlend = false;
    protected int blendSrcFactor = 1;
    protected int blendDstFactor = 1;
    protected int blendEquation;
    protected boolean rotate = false;
    protected float rotationSpeed = 1F;
    protected float[] rotationStatic = new float[] { 0, 0, 0 };
    protected float[] rotationAxis = new float[] { 0, 0, 0 };

    protected boolean showSun = true;
    protected boolean showMoon = true;
    protected boolean showStars = true;

    protected final Identifier MOON_PHASES = new Identifier("textures/environment/moon_phases.png");
    protected final Identifier SUN = new Identifier("textures/environment/sun.png");

    public SkyboxInstance(JsonObject json) {
        this.object = json;
    }

    public float getAlpha() {
        if (alwaysOn) {
            return 1F;
        }

        int currentTime = (int) Objects.requireNonNull(MinecraftClient.getInstance().world).getTimeOfDay() % 24000; // modulo so that it's bound to 24000
        int durationIn = Util.getTicksBetween(fade[0], fade[1]);
        int durationOut = Util.getTicksBetween(fade[2], fade[3]);

        int startFadeIn = fade[0] % 24000;
        int endFadeIn = fade[1] % 24000;

        if (endFadeIn < startFadeIn) {
            endFadeIn += 24000;
        }

        int startFadeOut = fade[2] % 24000;
        int endFadeOut = fade[3] % 24000;

        if (startFadeOut < endFadeIn) {
            startFadeOut += 24000;
        }

        if (endFadeOut < startFadeOut) {
            endFadeOut += 24000;
        }

        int tempInTime = currentTime;

        if (tempInTime < startFadeIn) {
            tempInTime += 24000;
        }

        int tempFullTime = currentTime;

        if (tempFullTime < endFadeIn) {
            tempFullTime += 24000;
        }

        int tempOutTime = currentTime;

        if (tempOutTime < startFadeOut) {
            tempOutTime += 24000;
        }

        float maxPossibleAlpha;

        if (startFadeIn < tempInTime && endFadeIn >= tempInTime) {
            maxPossibleAlpha = 1f - (((float) (endFadeIn - tempInTime)) / durationIn); // fading in
        } else if (endFadeIn < tempFullTime && startFadeOut >= tempFullTime) {
            maxPossibleAlpha = 1f; // fully faded in
        } else if (startFadeOut < tempOutTime && endFadeOut >= tempOutTime) {
            maxPossibleAlpha = (float) (endFadeOut - tempOutTime) / durationOut; // fading out
        } else {
            maxPossibleAlpha = 0f; // default not showing
        }

        return alpha = MathHelper.clamp(maxPossibleAlpha * maxAlpha, 0, 1);
    }

    protected int parseBlend(String str) {
        if (str == null) {
            return 1;
        } else {
            switch (str.toLowerCase(Locale.ENGLISH).trim()) {
                case "alpha":
                    return 0;
                case "add":
                    return 1;
                case "subtract":
                    return 2;
                case "multiply":
                    return 3;
                case "dodge":
                    return 4;
                case "burn":
                    return 5;
                case "screen":
                    return 6;
                case "overlay":
                    return 7;
                case "replace":
                    return 8;
                default:
                    AxolotlClient.LOGGER.warn("Unknown blend: " + str);
                    return 1;
            }
        }
    }

    protected void setupBlend(float brightness) {
        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
        if (manualBlend) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(blendSrcFactor, blendDstFactor);
            GL14.glBlendEquation(blendEquation);
            GlStateManager.enableTexture();
            return;
        }

        switch (blendMode) {
            case 0:

                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                break;

            case 1:

                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 1);
                break;

            case 2:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(775, 0);
                break;

            case 3:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(774, 771);
                break;

            case 4:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
                break;

            case 5:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(0, 769);
                break;

            case 6:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 769);
                break;

            case 7:
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(774, 768);
                break;

            case 8:
                GlStateManager.disableBlend();
                break;
        }
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);

        GlStateManager.enableTexture();
    }

    protected void clearBlend(float brightness) {
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0, 1);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
    }

    protected void setupRotate(float delta, float brightness) {
        GlStateManager.rotatef(0, rotationStatic[0], rotationStatic[1], rotationStatic[2]);
        if (rotate) {
            GlStateManager.rotatef(0, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
            //GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(MinecraftClient.getInstance().world.getSkyAngle(delta) * rotationSpeed, 0.0F, 1.0F,
                    0.0F);
            GlStateManager.rotatef(0, -rotationAxis[0], -rotationAxis[1], -rotationAxis[2]);
        }
    }

    protected void clearRotate() {
        GlStateManager.rotatef(0, -rotationStatic[0], -rotationStatic[1], -rotationStatic[2]);
    }

    public void render(float delta, float brightness) {
        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();

        setupBlend(brightness);
        setupRotate(delta, brightness);
        renderSkybox();
        renderDecorations(delta, brightness);
        clearBlend(brightness);
        clearRotate();

        GlStateManager.popMatrix();
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }

    protected void renderDecorations(float delta, float brightness) {
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(770, 1, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, brightness);
        GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(MinecraftClient.getInstance().world.getSkyAngle(delta) * 360.0F, 1.0F, 0.0F, 0.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        if (showSun) {
            float o = 30.0F;
            MinecraftClient.getInstance().getTextureManager().bindTexture(SUN);
            bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex((-o), 100.0, (-o)).texture(0.0, 0.0).next();
            bufferBuilder.vertex(o, 100.0, (-o)).texture(1.0, 0.0).next();
            bufferBuilder.vertex(o, 100.0, o).texture(1.0, 1.0).next();
            bufferBuilder.vertex(-o, 100.0, o).texture(0.0, 1.0).next();
            tessellator.draw();
        }
        if (showMoon) {
            float o = 20.0F;
            MinecraftClient.getInstance().getTextureManager().bindTexture(MOON_PHASES);
            int x = MinecraftClient.getInstance().world.getMoonPhase();
            int t = x % 4;
            int u = x / 4 % 2;
            float s = (float) (t) / 4.0F;
            float v = (float) (u) / 2.0F;
            float w = (float) (t + 1) / 4.0F;
            float y = (float) (u + 1) / 2.0F;
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex((-o), -100.0, o).texture(w, y).next();
            bufferBuilder.vertex(o, -100.0, o).texture(s, y).next();
            bufferBuilder.vertex(o, -100.0, (-o)).texture(s, v).next();
            bufferBuilder.vertex((-o), -100.0, (-o)).texture(w, v).next();
            tessellator.draw();
        }
        if (showStars) {
            GlStateManager.disableTexture();
            float z = MinecraftClient.getInstance().world.method_3707(delta) * brightness;
            if (z > 0.0F) {
                GlStateManager.color4f(z, z, z, z);
                if (((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getVbo()) {
                    VertexBuffer starsBuffer = ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer)
                            .getStarsBuffer();
                    starsBuffer.bind();
                    GL11.glEnableClientState(32884);
                    GL11.glVertexPointer(3, 5126, 12, 0L);
                    starsBuffer.draw(7);
                    starsBuffer.unbind();
                    GL11.glDisableClientState(32884);
                } else {
                    GlStateManager.callList(
                            ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getStarsList());
                }
            }
        }
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.popMatrix();
        GlStateManager.enableTexture();
    }

    public void remove() {
        for (Identifier id : textures) {
            try {
                MinecraftClient.getInstance().getTextureManager().close(id);
            } catch (Exception ignored) {}
        }
    }

    public abstract void renderSkybox();
}
