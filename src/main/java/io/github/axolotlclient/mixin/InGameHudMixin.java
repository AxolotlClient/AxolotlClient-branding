package io.github.axolotlclient.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.vanilla.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract boolean showCrosshair();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;color4f(FFFF)V", ordinal = 0))
    private void onHudRender(float tickDelta, CallbackInfo ci){
        HudManager.getInstance().render(MinecraftClient.getInstance(), tickDelta);
    }

    @Inject(method = "renderScoreboardObjective", at = @At("HEAD"), cancellable = true)
    public void customScoreBoard(ScoreboardObjective objective, Window window, CallbackInfo ci){
        ScoreboardHud hud = (ScoreboardHud) HudManager.getInstance().get(ScoreboardHud.ID);
        if(hud.isEnabled()){
            ci.cancel();
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;showCrosshair()Z"))
    public boolean noCrosshair(InGameHud instance){
        CrosshairHud hud = (CrosshairHud) HudManager.getInstance().get(CrosshairHud.ID);
        if(hud.isEnabled()) {
            GlStateManager.blendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlphaTest();
            return false;
        }
        return showCrosshair();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;III)I", ordinal = 0))
    public int actionBar(TextRenderer instance, String text, int x, int y, int color){
        ActionBarHud hud = (ActionBarHud) HudManager.getInstance().get(ActionBarHud.ID);
        if(hud.isEnabled()){
            hud.setActionBar(text, color);
            return 0;
        }
        return instance.draw(text, x, y, color);
    }

    @Inject(method = "renderBossBar", at = @At("HEAD"), cancellable = true)
    public void CustomBossBar(CallbackInfo ci){
        BossBarHud hud = (BossBarHud) HudManager.getInstance().get(BossBarHud.ID);
        if(hud.isEnabled()){
            ci.cancel();
        }
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    public void customHotbar(Window window, float tickDelta, CallbackInfo ci){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            ci.cancel();
        }
    }

    @ModifyArgs(method = "renderHeldItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Ljava/lang/String;FFI)I"))
    public void setItemNamePos(Args args){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            args.set(1, ((Integer) hud.getX()).floatValue() + (hud.getWidth() *hud.getScale() -
                    MinecraftClient.getInstance().textRenderer.getStringWidth(args.get(0)))/2);
            args.set(2, ((Integer) hud.getY()).floatValue() - 36 +
                    (!MinecraftClient.getInstance().interactionManager.hasStatusBars() ? 14 : 0));
        }
    }

    @ModifyArgs(method = "renderHorseHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V"))
    public void moveHorseHealth(Args args){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            args.set(0, hud.getX());
            args.set(1, hud.getY() - 7);
        }
    }

    @ModifyArgs(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(IIIIII)V"))
    public void moveXPBar(Args args){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            args.set(0, hud.getX());
            args.set(1, hud.getY()-7);
        }
    }

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHeight()I", ordinal = 1))
    public int moveXPBarHeight(Window instance){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            return hud.getY()+22;
        }
        return instance.getHeight();
    }

    @Redirect(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getWidth()I"))
    public int moveXPBarWidth(Window instance){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            return hud.getX()*2+ hud.getWidth();
        }
        return instance.getWidth();
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHeight()I"))
    public int moveStatusBarsHeight(Window instance){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            return hud.getY()+22;
        }
        return instance.getHeight();
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getWidth()I"))
    public int moveStatusBarsWidth(Window instance){
        HotbarHUD hud = (HotbarHUD) HudManager.getInstance().get(HotbarHUD.ID);
        if(hud.isEnabled()){
            return hud.getX()*2+ hud.getWidth();
        }
        return instance.getWidth();
    }
}
