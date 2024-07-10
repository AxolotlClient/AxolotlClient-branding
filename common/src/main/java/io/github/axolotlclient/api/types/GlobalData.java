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

package io.github.axolotlclient.api.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class GlobalData {
	public static final GlobalData EMPTY = new GlobalData(false, 0, 0, EncodedVersion.EMPTY, "");

	private final boolean success;
	private final int totalPlayers;
	private final int onlinePlayers;
	private final EncodedVersion latestVersion;
	private final String notes;

	public GlobalData(boolean success, int totalPlayers, int onlinePlayers, EncodedVersion latestVersion, String notes) {
		this.success = success;
		this.totalPlayers = totalPlayers;
		this.onlinePlayers = onlinePlayers;
		this.latestVersion = latestVersion;
		this.notes = notes;
	}
}
