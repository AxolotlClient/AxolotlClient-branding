/*
 * Copyright © 2021-2024 moehreag <moehreag@gmail.com> & Contributors
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

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class Persistence {
	protected abstract Type type();

	public int count() {
		throw new UnsupportedOperationException();
	}
	public long duration() {
		throw new UnsupportedOperationException();
	}
	public static class Channel extends Persistence {
		@Override
		protected Type type() {
			return Type.CHANNEL;
		}
	}
	@AllArgsConstructor
	private static class Duration extends Persistence {

		protected final long duration;

		@Override
		protected Type type() {
			return Type.DURATION;
		}

		@Override
		public long duration() {
			return duration;
		}

		@Override
		public Map<String, Object> toJson() {
			Map<String, Object> map = super.toJson();
			map.put("duration", duration);
			return map;
		}
	}
	@AllArgsConstructor
	private static class Count extends Persistence {

		private final int count;

		@Override
		protected Type type() {
			return Type.COUNT;
		}

		@Override
		public int count() {
			return count;
		}

		@Override
		public Map<String, Object> toJson() {
			Map<String, Object> map = super.toJson();
			map.put("count", count);
			return map;
		}
	}
	@AllArgsConstructor
	private static class CountDuration extends Persistence {
		private final int count;
		private final long duration;
		@Override
		protected Type type() {
			return Type.COUNT_DURATION;
		}

		@Override
		public int count() {
			return count;
		}

		@Override
		public long duration() {
			return duration;
		}

		@Override
		public Map<String, Object> toJson() {
			Map<String, Object> map = super.toJson();
			map.put("count", count);
			map.put("duration", duration);
			return map;
		}
	}

	@AllArgsConstructor
	@Getter
	public enum Type {
		CHANNEL("channel"),
		COUNT("count"),
		DURATION("duration"),
		COUNT_DURATION("count_duration")
		;
		private final String id;
	}

	public static Persistence of(Type type, int count, long duration) {
		return switch (type) {
			case CHANNEL -> new Channel();
			case COUNT -> new Count(count);
			case DURATION -> new Duration(duration);
			case COUNT_DURATION -> new CountDuration(count, duration);
		};
	}

	@SuppressWarnings("unchecked")
	public static Persistence fromJson(Object json) {
		Map<String, ?> map = (Map<String, ?>) json;

		return switch ((String) map.get("type")) {
			case "channel" -> new Channel();
			case "count" -> new Count((int) map.get("count"));
			case "duration" -> new Duration((long) map.get("duration"));
			case "count_duration" -> new CountDuration((int) map.get("count"), (long) map.get("duration"));
			default -> throw new IllegalArgumentException(json.toString());
		};
	}

	public Map<String, Object> toJson() {
		Map<String, Object> map = new HashMap<>();
		map.put("type", type().id);
		return map;
	}
}
