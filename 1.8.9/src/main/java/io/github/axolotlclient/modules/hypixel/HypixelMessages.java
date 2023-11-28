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

package io.github.axolotlclient.modules.hypixel;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.util.GsonHelper;
import io.github.axolotlclient.util.events.impl.ReceiveChatMessageEvent;
import io.github.moehreag.searchInResources.SearchableResourceManager;
import lombok.Getter;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.legacyfabric.fabric.api.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;

public class HypixelMessages implements IdentifiableResourceReloadListener {

	@Getter
	private static final HypixelMessages instance = new HypixelMessages();

	@Getter
	private final Map<String, Map<String, Pattern>> languageMessageMap = new HashMap<>();
	private final Map<String, Map<String, Pattern>> messageLanguageMap = new HashMap<>();

	public void load() {
		languageMessageMap.clear();
		messageLanguageMap.clear();

		AxolotlClient.LOGGER.debug("Loading Hypixel Messages");
		ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
		((SearchableResourceManager) manager).findResources("axolotlclient", "lang",
			identifier -> identifier.getPath().endsWith(".hypixel.json")).values().forEach(resource -> {
			int i = resource.getId().getPath().lastIndexOf("/") + 1;
			String lang = resource.getId().getPath().substring(i, i + 5);
			JsonObject lines = GsonHelper.GSON.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
			AxolotlClient.LOGGER.debug("Found message file: " + resource.getId());
			languageMessageMap.put(lang, new HashMap<>());
			Map<String, Pattern> map = languageMessageMap.get(lang);
			lines.entrySet().forEach(entry -> {
				Pattern pattern = Pattern.compile(entry.getValue().getAsString());
				map.putIfAbsent(entry.getKey(), pattern);
				messageLanguageMap.putIfAbsent(entry.getKey(), new HashMap<>());
				messageLanguageMap.get(entry.getKey()).put(lang, pattern);
			});
		});
	}

	public void process(BooleanOption option, String messageKey, ReceiveChatMessageEvent event) {
		if (option.get() && matchesAnyLanguage(messageKey, event.getOriginalMessage())) {
			event.setCancelled(true);
		}
	}

	public boolean matchesAnyLanguage(String key, String message) {
		return messageLanguageMap.get(key).values().stream().map(pattern -> pattern.matcher(message)).anyMatch(Matcher::matches);
	}

	public boolean matchesAnyMessage(String lang, String message) {
		return languageMessageMap.get(lang).values().stream().map(pattern -> pattern.matcher(message)).anyMatch(Matcher::matches);
	}

	public boolean matchesAny(String message) {
		return languageMessageMap.values().stream().map(Map::values).anyMatch(patterns -> patterns.stream()
			.map(pattern -> pattern.matcher(message)).anyMatch(Matcher::matches));
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("axolotlclient", "hypixel_messages");
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		load();
	}
}
