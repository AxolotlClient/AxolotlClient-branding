/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.screenshotUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.axolotlclient.util.Util;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

public class ImageShare extends ImageNetworking {

	@Getter
	private static final ImageShare Instance = new ImageShare();

	private ImageShare() {
	}

	public void uploadImage(Path file) {
		Util.sendChatMessage(Component.translatable("imageUploadStarted"));
		upload(file).whenCompleteAsync((downloadUrl, throwable) -> {
			if (downloadUrl.isEmpty()) {
				Util.sendChatMessage(Component.translatable("imageUploadFailure"));
			} else {
				Util.sendChatMessage(Component.translatable("imageUploadSuccess").append(" ").append(
					Component.literal(downloadUrl).setStyle(
						Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, downloadUrl))
							.withHoverEvent(
								new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("clickToCopy"))))));
			}
		});
	}

	public ImageInstance downloadImage(String url) {
		ImageData data = download(url);
		if (data != ImageData.EMPTY) {
			try {
				ImageInstance.Remote remote = new ImageInstance.RemoteImpl(NativeImage.read(new ByteArrayInputStream(data.data())), data.name(), data.uploader(), data.sharedAt(), url);
				try {
					Path local = GalleryScreen.SCREENSHOT_DIR.resolve(remote.filename());
					HashFunction hash = Hashing.goodFastHash(32);
					if (Files.exists(local) && hash.hashBytes(data.data()).equals(hash.hashBytes(Files.readAllBytes(local)))) {
						return remote.toShared(local);
					}
				} catch (IOException ignored) {
				}
				return remote;
			} catch (IOException ignored) {
			}
		}
		return null;
	}
}
