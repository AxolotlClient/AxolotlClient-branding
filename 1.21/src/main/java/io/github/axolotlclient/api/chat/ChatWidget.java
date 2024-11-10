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

package io.github.axolotlclient.api.chat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.ContextMenu;
import io.github.axolotlclient.api.ContextMenuScreen;
import io.github.axolotlclient.api.handlers.ChatHandler;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.types.Channel;
import io.github.axolotlclient.api.types.ChatMessage;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.util.ClientColors;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.list.AlwaysSelectedEntryListWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChatWidget extends AlwaysSelectedEntryListWidget<ChatWidget.ChatLine> {

	private final List<ChatMessage> messages = new ArrayList<>();
	private final Channel channel;
	private final MinecraftClient client;
	private final ContextMenuScreen screen;
	@Setter
	@Getter
	private int x, y, width, height;

	public ChatWidget(Channel channel, int x, int y, int width, int height, ContextMenuScreen screen) {
		super(MinecraftClient.getInstance(), width, height, y, 13);
		this.channel = channel;
		this.client = MinecraftClient.getInstance();
		setX(x + 5);

		setRenderHeader(false, 0);
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		Arrays.stream(channel.getMessages()).forEach(this::addMessage);

		ChatHandler.getInstance().setMessagesConsumer(chatMessages -> chatMessages.forEach(this::addMessage));
		ChatHandler.getInstance().setMessageConsumer(this::addMessage);
		ChatHandler.getInstance().setEnableNotifications(message -> !Arrays.stream(channel.getUsers()).collect(Collectors.toUnmodifiableSet()).contains(message.getSender()));

		setScrollAmount(getMaxScroll());
	}

	@Override
	protected int getScrollbarPositionX() {
		return x + width - 5;
	}

	private void addMessage(ChatMessage message) {
		List<OrderedText> list = client.textRenderer.wrapLines(Text.of(message.getContent()), getRowWidth());

		boolean scrollToBottom = getScrollAmount() == getMaxScroll();

		if (messages.size() > 0) {
			ChatMessage prev = messages.get(messages.size() - 1);
			if (!prev.getSender().equals(message.getSender())) {
				addEntry(new NameChatLine(message));
			} else {
				if (prev.getTimestamp() - message.getTimestamp() > 150) {
					addEntry(new NameChatLine(message));
				}
			}
		} else {
			addEntry(new NameChatLine(message));
		}

		list.forEach(t -> addEntry(new ChatLine(t, message)));
		messages.add(message);

		children().sort(Comparator.comparingLong(c -> c.getOrigin().getTimestamp()));

		if (scrollToBottom) {
			setScrollAmount(getMaxScroll());
		}
		messages.sort(Comparator.comparingLong(ChatMessage::getTimestamp));
	}

	private void loadMessages() {
		long before;
		if (messages.size() > 0) {
			before = messages.get(0).getTimestamp();
		} else {
			before = Instant.now().getEpochSecond();
		}
		ChatHandler.getInstance().getMessagesBefore(channel, before);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		double scrollAmount = (this.getScrollAmount() - amountY * (double) this.itemHeight / 2.0);
		if (scrollAmount < 0) {
			loadMessages();
		}
		setScrollAmount(scrollAmount);
		return true;
	}

	public void remove() {
		ChatHandler.getInstance().setMessagesConsumer(ChatHandler.DEFAULT_MESSAGES_CONSUMER);
		ChatHandler.getInstance().setMessageConsumer(ChatHandler.DEFAULT_MESSAGE_CONSUMER);
		ChatHandler.getInstance().setEnableNotifications(ChatHandler.DEFAULT);
	}

	@Override
	protected void drawEntrySelectionHighlight(GuiGraphics graphics, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
		int i = this.getX() + (this.width - entryWidth) / 2;
		int j = this.getX() + (this.width + entryWidth) / 2;
		graphics.fill(i, y - 2, j, y + entryHeight, borderColor);
		graphics.fill(i + 1, y - 1, j - 1, y + entryHeight - 1, fillColor);
	}

	@Override
	protected boolean isZero(int index) {
		return true;
	}

	public class ChatLine extends AlwaysSelectedEntryListWidget.Entry<ChatLine> {
		protected final MinecraftClient client = MinecraftClient.getInstance();
		@Getter
		private final OrderedText content;
		@Getter
		private final ChatMessage origin;

		public ChatLine(OrderedText content, ChatMessage origin) {
			super();
			this.content = content;
			this.origin = origin;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 0) {
				ChatWidget.this.setSelected(this);
				return true;
			}
			if (button == 1) {
				ContextMenu.Builder builder = ContextMenu.builder()
					.entry(Text.of(origin.getSender().getName()), buttonWidget -> {
					})
					.spacer();
				if (!origin.getSender().equals(API.getInstance().getSelf())) {
					builder.entry(Text.translatable("api.friends.chat"), buttonWidget -> {
							ChannelRequest.getOrCreateDM(origin.getSender().getUuid())
								.whenCompleteAsync((channel, throwable) -> client.setScreen(new ChatScreen(screen.getParent(), channel)));
						})
						.spacer();
				}
				builder.entry(Text.translatable("api.chat.report.message"), buttonWidget -> {
						ChatHandler.getInstance().reportMessage(origin);
					})
					.spacer()
					.entry(Text.translatable("action.copy"), buttonWidget -> {
						client.keyboard.setClipboard(origin.getContent());
					});
				screen.setContextMenu(builder.build());
				return true;
			}
			return false;
		}

		protected void renderExtras(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
		}

		@Override
		public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			for (ChatLine l : children()) {
				if (l.getOrigin().equals(origin)) {
					if (Objects.equals(getHoveredEntry(), l)) {
						hovered = true;
						break;
					}
				}
			}
			if (hovered && !screen.hasContextMenu()) {
				graphics.fill(x - 2 - 22, y - 2, x + entryWidth + 20, y + entryHeight - 1, 0x33FFFFFF);
				if (index < children().size() - 1 && children().get(index + 1).getOrigin().equals(origin)) {
					graphics.fill(x - 2 - 22, y + entryHeight - 1, x + entryWidth + 20, y + entryHeight + 2, 0x33FFFFFF);
				}
				if ((index < children().size() - 1 && !children().get(index + 1).getOrigin().equals(origin)) || index == children().size() - 1) {
					graphics.fill(x - 2 - 22, y + entryHeight - 1, x + entryWidth + 20, y + entryHeight, 0x33FFFFFF);
				}
			}
			renderExtras(graphics, x, y, mouseX, mouseY);
			graphics.drawText(MinecraftClient.getInstance().textRenderer, content, x, y, -1, false);
		}

		@Override
		public Text getNarration() {
			return Text.of(origin.getContent());
		}
	}

	public class NameChatLine extends ChatLine {

		private final String formattedTime;

		public NameChatLine(ChatMessage message) {
			super(Text.literal(message.getSenderDisplayName())
				.setStyle(Style.EMPTY.withBold(true)).asOrderedText(), message);

			SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d/M/yyyy H:mm");
			formattedTime = DATE_FORMAT.format(new Date(message.getTimestamp() * 1000));
		}

		@Override
		protected void renderExtras(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
			RenderSystem.disableBlend();
			Identifier texture = Auth.getInstance().getSkinTexture(getOrigin().getSender().getUuid(),
				getOrigin().getSender().getName());
			graphics.drawTexture(texture, x - 22, y, 18, 18, 8, 8, 8, 8, 64, 64);
			graphics.drawTexture(texture, x - 22, y, 18, 18, 40, 8, 8, 8, 64, 64);
			RenderSystem.enableBlend();
			graphics.drawText(client.textRenderer, formattedTime, client.textRenderer.getWidth(getContent()) + x + 5, y, ClientColors.GRAY.toInt(), false);
		}
	}
}
