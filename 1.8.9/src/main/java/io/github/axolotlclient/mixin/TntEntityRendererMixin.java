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

import io.github.axolotlclient.modules.tnttime.TntTime;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TntRenderer;
import net.minecraft.entity.PrimedTntEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TntRenderer.class)
public abstract class TntEntityRendererMixin extends EntityRenderer<PrimedTntEntity> {

	protected TntEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Inject(method = "render(Lnet/minecraft/entity/PrimedTntEntity;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;DDDFF)V"), cancellable = true)
	public void axolotlclient$render(PrimedTntEntity entity, double x, double y, double z, float g, float h, CallbackInfo ci) {
		if (TntTime.getInstance().enabled.get()) {
			super.renderNameTag(entity, TntTime.getInstance().getFuseTime(entity.fuseTimer).getFormattedString(),
				x, y, z, 64);
			ci.cancel();
		}
	}
}
