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

package io.github.axolotlclient.modules.hypixel.autoboop;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.hypixel.AbstractHypixelMod;
import io.github.axolotlclient.util.Util;
import io.github.axolotlclient.util.events.Events;
import io.github.axolotlclient.util.events.impl.ReceiveChatMessageEvent;
import lombok.Getter;

public class AutoBoop implements AbstractHypixelMod {

	@Getter
	private final static AutoBoop Instance = new AutoBoop();

	protected OptionCategory cat = new OptionCategory("autoBoop");
	protected BooleanOption enabled = new BooleanOption("enabled", "autoBoop", false);

	@Override
	public void init() {
		cat.add(enabled);
		Events.RECEIVE_CHAT_MESSAGE_EVENT.register(this::onMessage);
	}

	@Override
	public OptionCategory getCategory() {
		return cat;
	}

	public void onMessage(ReceiveChatMessageEvent event) {
		String message = event.getOriginalMessage().trim();
		if (enabled.get() && message.contains("Friend >")
			&& message.contains("joined.")) {
			System.out.println(message);
			String player = message.substring(message.indexOf("Friend >"),
				message.lastIndexOf(" "));
			System.out.println(player);
			Util.sendChatMessage("/boop " + player);
			AxolotlClient.LOGGER.info("Booped " + player);
		}
	}
}
