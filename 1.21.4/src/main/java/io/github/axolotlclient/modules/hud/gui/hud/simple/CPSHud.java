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

package io.github.axolotlclient.modules.hud.gui.hud.simple;

import java.util.ArrayList;
import java.util.List;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.modules.hud.gui.entry.SimpleTextHudEntry;
import io.github.axolotlclient.util.events.Events;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */

public class CPSHud extends SimpleTextHudEntry {

	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("kronhud", "cpshud");

	private final BooleanOption fromKeybindings = new BooleanOption("cpskeybind", false);
	private final BooleanOption rmb = new BooleanOption("rightcps", false);

	public CPSHud() {
		super();
		Events.MOUSE_INPUT.register(event -> {
			if (!fromKeybindings.get()) {
				if (event.getButton() == 0) {
					ClickList.LEFT.click();
				} else if (event.getButton() == 1) {
					ClickList.RIGHT.click();
				}
			}
		});
		Events.KEY_PRESS.register(event -> {
			if (fromKeybindings.get()) {
				if (event.key().equals(client.options.keyAttack)) {
					ClickList.LEFT.click();
				} else if (event.key().equals(client.options.keyUse)) {
					ClickList.RIGHT.click();
				}
			}
		});
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public void tick() {
		ClickList.LEFT.update();
		ClickList.RIGHT.update();
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(fromKeybindings);
		options.add(rmb);
		return options;
	}

	@Override
	public String getValue() {
		if (rmb.get()) {
			return ClickList.LEFT.clicks() + " | " + ClickList.RIGHT.clicks() + " CPS";
		} else {
			return ClickList.LEFT.clicks() + " CPS";
		}
	}

	@Override
	public String getPlaceholder() {
		if (rmb.get()) {
			return "0 | 0 CPS";
		} else {
			return "0 CPS";
		}
	}

	public static class ClickList {

		public static final ClickList LEFT = new ClickList();
		public static final ClickList RIGHT = new ClickList();
		private final List<Long> clicks;

		public ClickList() {
			clicks = new ArrayList<>();
		}

		public void update() {
			clicks.removeIf((click) -> Util.getMillis() - click > 1000);
		}

		public void click() {
			clicks.add(Util.getMillis());
		}

		public int clicks() {
			return clicks.size();
		}
	}
}
