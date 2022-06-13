package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.modules.hud.HudManager;
import io.github.moehreag.axolotlclient.modules.hud.gui.hud.PackDisplayHud;
import io.github.moehreag.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceReload;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class MixinReloadableResourceManager {

    @Inject(method = "reload", at=@At("TAIL"))
    public void loadSkies(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> resourcePacks, CallbackInfoReturnable<ResourceReload> cir){
        HypixelAbstractionLayer.clearPlayerData();
        //if(AxolotlClient.initalized)SkyResourceManager.reload(resourcePacks);
        //else{SkyResourceManager.packs=resourcePacks;}

        PackDisplayHud hud = (PackDisplayHud) HudManager.getINSTANCE().get(PackDisplayHud.ID);
        if(hud.isEnabled()){
            hud.widgets.clear();
        }

        AxolotlClient.packs=resourcePacks;
    }

    @Inject(method = "method_14486", at = @At("HEAD"), cancellable = true)
    public void getResource(Identifier id, CallbackInfoReturnable<Resource> cir){
        if(AxolotlClient.runtimeResources.get(id) != null){
            cir.setReturnValue(AxolotlClient.runtimeResources.get(id));
        }
    }

}
