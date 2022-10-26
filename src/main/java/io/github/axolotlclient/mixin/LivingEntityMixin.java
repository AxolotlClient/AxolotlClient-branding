package io.github.axolotlclient.mixin;

import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hud.gui.hud.simple.ComboHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;lastDamageTime:J"))
    private void onDamage(DamageSource source, float damage, CallbackInfoReturnable<Boolean> ci) {
        // The client doesn't really get any sort of information about why a person is damaged
        // Kinda sucks since that means combos can't be guarenteed (i.e. fall damage, or other person hits)
        // Possible fixes: Could wait for swing animation from a player to be played. Could then track eyes to see if hit, give or take
        // 2 ticks or so? Defintely not perfect tho
        if(source.getAttacker() instanceof PlayerEntity){
            ComboHud comboHud = (ComboHud) HudManager.getInstance().get(ComboHud.ID);
            comboHud.onEntityDamage(this);
        }
    }
}
