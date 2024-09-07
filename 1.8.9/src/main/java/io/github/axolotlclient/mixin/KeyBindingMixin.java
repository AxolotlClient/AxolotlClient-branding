/*
 * Copyright © 2021-2023 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.events.Events;
import io.github.axolotlclient.util.events.impl.KeyBindChangeEvent;
import io.github.axolotlclient.util.events.impl.KeyPressEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Int2ObjectHashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

	@Shadow
	@Final
	private static Int2ObjectHashMap<KeyBinding> BY_KEY_CODE;
	@Shadow
	private boolean pressed;
	@Shadow
	private int keyCode;

	@Inject(method = "set", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/KeyBinding;pressed:Z"))
	private static void axolotlclient$onPress(int keyCode, boolean pressed, CallbackInfo ci) {
		if (pressed) {
			Events.KEY_PRESS.invoker().invoke(new KeyPressEvent(BY_KEY_CODE.get(keyCode)));
		}
	}

	@Inject(method = "isPressed", at = @At("HEAD"))
	public void axolotlclient$noMovementFixAfterInventory(CallbackInfoReturnable<Boolean> cir) {
		if (this.keyCode == Minecraft.getInstance().options.sneakKey.getKeyCode()
			|| keyCode == Minecraft.getInstance().options.forwardKey.getKeyCode()
			|| keyCode == Minecraft.getInstance().options.backKey.getKeyCode()
			|| keyCode == Minecraft.getInstance().options.rightKey.getKeyCode()
			|| keyCode == Minecraft.getInstance().options.leftKey.getKeyCode()
			|| keyCode == Minecraft.getInstance().options.jumpKey.getKeyCode()
			|| keyCode == Minecraft.getInstance().options.sprintKey.getKeyCode()) {
			this.pressed = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode)) &&
						   (Minecraft.getInstance().screen == null);
		}
	}

	@Inject(method = "setKeyCode", at = @At("RETURN"))
	public void axolotlclient$boundKeySet(int code, CallbackInfo ci) {
		Events.KEYBIND_CHANGE.invoker().invoke(new KeyBindChangeEvent(code));
	}
}
