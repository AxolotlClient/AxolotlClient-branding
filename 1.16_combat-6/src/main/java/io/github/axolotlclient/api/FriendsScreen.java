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

package io.github.axolotlclient.api;

import java.util.stream.Collectors;

import io.github.axolotlclient.api.chat.ChatScreen;
import io.github.axolotlclient.api.requests.FriendRequest;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.util.AlphabeticalComparator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class FriendsScreen extends Screen {

	private final Screen parent;

	private UserListWidget widget;

	private ButtonWidget chatButton, removeButton, onlineTab, allTab, pendingTab, blockedTab;
	private ButtonWidget denyButton, acceptButton;

	private Tab current = Tab.ONLINE;

	protected FriendsScreen(Screen parent, Tab tab) {
		this(parent);
		current = tab;
	}

	protected FriendsScreen(Screen parent) {
		super(new TranslatableText("api.screen.friends"));
		this.parent = parent;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.widget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == 294) {
			this.refresh();
			return true;
		} else if (this.widget.getSelected() != null) {
			if (keyCode != 257 && keyCode != 335) {
				return this.widget.keyPressed(keyCode, scanCode, modifiers);
			} else {
				this.openChat();
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void init() {
		addChild(widget = new UserListWidget(this, client, width, height, 32, height - 64, 35));

		widget.children().clear();

		if (current == Tab.ALL || current == Tab.ONLINE) {
			FriendRequest.getInstance().getFriends().whenCompleteAsync((list, t) -> widget.setUsers(list.stream().sorted((u1, u2) ->
				new AlphabeticalComparator().compare(u1.getName(), u2.getName())).filter(user -> {
				if (current == Tab.ONLINE) {
					return user.getStatus().isOnline();
				}
				return true;
			}).collect(Collectors.toList())));
		} else if (current == Tab.PENDING) {
			FriendRequest.getInstance().getFriendRequests().whenCompleteAsync((con, th) -> {

				con.getLeft().stream().sorted((u1, u2) -> new AlphabeticalComparator().compare(u1.getName(), u2.getName()))
					.forEach(user -> widget.addEntry(new UserListWidget.UserListEntry(user, new TranslatableText("api.friends.pending.incoming"))));
				con.getRight().stream().sorted((u1, u2) -> new AlphabeticalComparator().compare(u1.getName(), u2.getName()))
					.forEach(user -> widget.addEntry(new UserListWidget.UserListEntry(user, new TranslatableText("api.friends.pending.outgoing"))));
			});
		} else if (current == Tab.BLOCKED) {
			FriendRequest.getInstance().getBlocked().whenCompleteAsync((list, th) -> widget.setUsers(list.stream().sorted((u1, u2) ->
				new AlphabeticalComparator().compare(u1.getName(), u2.getName())).collect(Collectors.toList())));
		}

		this.addButton(blockedTab = new ButtonWidget(this.width / 2 + 24, this.height - 52, 57, 20,
			new TranslatableText("api.friends.tab.blocked"), button ->
			client.openScreen(new FriendsScreen(parent, Tab.BLOCKED))));

		this.addButton(pendingTab = new ButtonWidget(this.width / 2 - 34, this.height - 52, 57, 20,
			new TranslatableText("api.friends.tab.pending"), button ->
			client.openScreen(new FriendsScreen(parent, Tab.PENDING))));

		this.addButton(allTab = new ButtonWidget(this.width / 2 - 94, this.height - 52, 57, 20,
			new TranslatableText("api.friends.tab.all"), button ->
			client.openScreen(new FriendsScreen(parent, Tab.ALL))));

		this.addButton(onlineTab = new ButtonWidget(this.width / 2 - 154, this.height - 52, 57, 20,
			new TranslatableText("api.friends.tab.online"), button ->
			client.openScreen(new FriendsScreen(parent, Tab.ONLINE))));

		this.addButton(new ButtonWidget(this.width / 2 + 88, this.height - 52, 66, 20,
			new TranslatableText("api.friends.add"),
			button -> client.openScreen(new AddFriendScreen(this))));

		this.removeButton = this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 28, 100, 20,
			new TranslatableText("api.friends.remove"), button -> {
			UserListWidget.UserListEntry entry = this.widget.getSelected();
			if (entry != null) {
				FriendRequest.getInstance().removeFriend(entry.getUser());
				refresh();
			}
		}));

		addButton(denyButton = new ButtonWidget(this.width / 2 - 50, this.height - 28, 48, 20,
			new TranslatableText("api.friends.request.deny"),
			button -> denyRequest()));

		addButton(acceptButton = new ButtonWidget(this.width / 2 + 2, this.height - 28, 48, 20,
			new TranslatableText("api.friends.request.accept"),
			button -> acceptRequest()));

		this.addButton(chatButton = new ButtonWidget(this.width / 2 - 154, this.height - 28, 100, 20,
			new TranslatableText("api.friends.chat"), button -> openChat()));

		this.addButton(
			new ButtonWidget(this.width / 2 + 4 + 50, this.height - 28, 100, 20,
				ScreenTexts.BACK, button -> this.client.openScreen(this.parent)));
		updateButtonActivationStates();
	}

	private void refresh() {
		client.openScreen(new FriendsScreen(parent));
	}

	private void denyRequest() {
		UserListWidget.UserListEntry entry = widget.getSelected();
		if (entry != null) {
			FriendRequest.getInstance().denyFriendRequest(entry.getUser());
		}
		refresh();
	}

	private void acceptRequest() {
		UserListWidget.UserListEntry entry = widget.getSelected();
		if (entry != null) {
			FriendRequest.getInstance().acceptFriendRequest(entry.getUser());
		}
		refresh();
	}

	private void updateButtonActivationStates() {
		UserListWidget.UserListEntry entry = widget.getSelected();
		if (entry != null) {
			chatButton.active = removeButton.active = true;
		} else {
			chatButton.active = removeButton.active = false;
		}

		removeButton.visible = true;
		denyButton.visible = false;
		acceptButton.visible = false;
		if (current == Tab.ONLINE) {
			onlineTab.active = false;
			allTab.active = pendingTab.active = blockedTab.active = true;
		} else if (current == Tab.ALL) {
			allTab.active = false;
			onlineTab.active = pendingTab.active = blockedTab.active = true;
		} else if (current == Tab.PENDING) {
			pendingTab.active = false;
			onlineTab.active = allTab.active = blockedTab.active = true;
			removeButton.visible = false;
			denyButton.visible = true;
			acceptButton.visible = true;
		} else if (current == Tab.BLOCKED) {
			blockedTab.active = false;
			onlineTab.active = allTab.active = pendingTab.active = true;
		}
	}

	public void openChat() {
		UserListWidget.UserListEntry entry = widget.getSelected();
		if (entry != null) {
			ChannelRequest.getOrCreateDM(entry.getUser().getUuid())
				.whenCompleteAsync((c, t) -> client.openScreen(new ChatScreen(this, c)));
		}
	}

	public void select(UserListWidget.UserListEntry entry) {
		this.widget.setSelected(entry);
		this.updateButtonActivationStates();
	}

	public enum Tab {
		ONLINE,
		ALL,
		PENDING,
		BLOCKED
	}
}
