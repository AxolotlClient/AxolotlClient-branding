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

package io.github.axolotlclient.api;

import java.util.stream.Collectors;

import io.github.axolotlclient.api.chat.ChatScreen;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.requests.FriendRequest;
import io.github.axolotlclient.api.util.AlphabeticalComparator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.LanScanWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;

public class FriendsScreen extends Screen {

	private final Screen parent;

	private UserListWidget widget;

	private ButtonWidget chatButton, removeButton, onlineTab, allTab, pendingTab, blockedTab;
	private ButtonWidget denyButton, acceptButton, unblockButton, cancelButton;

	private Tab current = Tab.ONLINE;

	protected FriendsScreen(Screen parent, Tab tab) {
		this(parent);
		current = tab;
	}

	public FriendsScreen(Screen parent) {
		super();
		this.parent = parent;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.widget.render(mouseX, mouseY, delta);
		drawCenteredString(this.textRenderer, I18n.translate("api.screen.friends"), this.width / 2, 20, 16777215);
		super.render(mouseX, mouseY, delta);
	}

	@Override
	protected void keyPressed(char c, int i) {
		int j = this.widget.getSelected();
		EntryListWidget.Entry entry = j < 0 ? null : this.widget.getEntry(j);
		if (i == 63) {
			this.refresh();
		} else {
			if (j >= 0) {
				if (i == 200) {
					if (isShiftDown()) {
						if (j > 0 && entry instanceof UserListWidget.UserListEntry) {
							this.select(this.widget.getSelected() - 1);
							this.widget.scroll(-this.widget.getEntryHeight());
						}
					} else if (j > 0) {
						this.select(this.widget.getSelected() - 1);
						this.widget.scroll(-this.widget.getEntryHeight());
						if (this.widget.getEntry(this.widget.getSelected()) instanceof LanScanWidget) {
							if (this.widget.getSelected() > 0) {
								this.select(this.widget.size() - 1);
								this.widget.scroll(-this.widget.getEntryHeight());
							} else {
								this.select(-1);
							}
						}
					} else {
						this.select(-1);
					}
				} else if (i == 208) {
					if (isShiftDown()) {
						this.select(j + 1);
						this.widget.scroll(this.widget.getEntryHeight());
					} else if (j < this.widget.size()) {
						this.select(this.widget.getSelected() + 1);
						this.widget.scroll(this.widget.getEntryHeight());
						if (this.widget.getEntry(this.widget.getSelected()) instanceof LanScanWidget) {
							if (this.widget.getSelected() < this.widget.size() - 1) {
								this.select(this.widget.size() + 1);
								this.widget.scroll(this.widget.getEntryHeight());
							} else {
								this.select(-1);
							}
						}
					} else {
						this.select(-1);
					}
				} else if (i != 28 && i != 156) {
					super.keyPressed(c, i);
				} else {
					this.buttonClicked(this.buttons.get(2));
				}
			} else {
				super.keyPressed(c, i);
			}
		}
	}

	private void refresh() {
		minecraft.openScreen(new FriendsScreen(parent));
	}

	public void select(int i) {
		this.widget.setSelected(i);
		this.updateButtonActivationStates();
	}

	public void openChat() {
		if (!chatButton.active) {
			return;
		}
		UserListWidget.UserListEntry entry = widget.getSelectedEntry();
		if (entry != null) {
			chatButton.active = false;
			ChannelRequest.getOrCreateDM(entry.getUser())
				.thenAccept(c -> minecraft.submit(() -> minecraft.openScreen(new ChatScreen(this, c))));
		}
	}

	private void acceptRequest() {
		UserListWidget.UserListEntry entry = widget.getSelectedEntry();
		if (entry != null) {
			acceptButton.active = false;
			FriendRequest.getInstance().acceptFriendRequest(entry.getUser()).thenRun(() -> minecraft.submit(this::refresh));
		}
	}

	private void denyRequest() {
		UserListWidget.UserListEntry entry = widget.getSelectedEntry();
		if (entry != null) {
			denyButton.active = false;
			FriendRequest.getInstance().denyFriendRequest(entry.getUser()).thenRun(() -> minecraft.submit(this::refresh));
		}
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		this.widget.mouseClicked(i, j, k);
	}

	@Override
	protected void mouseReleased(int i, int j, int k) {
		super.mouseReleased(i, j, k);
		this.widget.mouseReleased(i, j, k);
	}

	@Override
	protected void buttonClicked(ButtonWidget buttonWidget) {
		switch (buttonWidget.id) {
			case 0:
				this.minecraft.openScreen(this.parent);
				break;
			case 1:
				openChat();
				break;
			case 2:
				acceptRequest();
				break;
			case 3:
				denyRequest();
				break;
			case 4:
				UserListWidget.UserListEntry entry = this.widget.getSelectedEntry();
				if (entry != null) {
					removeButton.active = false;
					FriendRequest.getInstance().removeFriend(entry.getUser()).thenRun(() -> minecraft.submit(this::refresh));
				}
				break;
			case 5:
				minecraft.openScreen(new AddFriendScreen(this));
				break;
			case 6:
				minecraft.openScreen(new FriendsScreen(parent, Tab.ONLINE));
				break;
			case 7:
				minecraft.openScreen(new FriendsScreen(parent, Tab.ALL));
				break;
			case 8:
				minecraft.openScreen(new FriendsScreen(parent, Tab.PENDING));
				break;
			case 9:
				minecraft.openScreen(new FriendsScreen(parent, Tab.BLOCKED));
				break;
			case 10:
				buttonWidget.active = false;
				FriendRequest.getInstance().unblockUser(widget.getSelectedEntry().getUser()).thenRun(() -> minecraft.submit(this::refresh));
				break;
			case 11:
				buttonWidget.active = false;
				FriendRequest.getInstance().cancelFriendRequest(widget.getSelectedEntry().getUser()).thenRun(() -> minecraft.submit(this::refresh));
				break;
		}
	}

	@Override
	public void init() {
		widget = new UserListWidget(this, minecraft, width, height, 32, height - 64, 35);

		if (current == Tab.ALL || current == Tab.ONLINE) {
			FriendRequest.getInstance().getFriends().whenCompleteAsync((list, t) -> widget.setUsers(list.stream().filter(user -> {
				if (current == Tab.ONLINE) {
					return user.getStatus().isOnline();
				}
				return true;
			}).sorted((u1, u2) -> new AlphabeticalComparator().compare(u1.getName(), u2.getName())).collect(Collectors.toList())));
		} else if (current == Tab.PENDING) {
			FriendRequest.getInstance().getFriendRequests().whenCompleteAsync((con, th) -> {

				con.getLeft().stream().sorted((u1, u2) -> new AlphabeticalComparator().compare(u1.getName(), u2.getName()))
					.forEach(user -> widget.addEntry(new UserListWidget.UserListEntry(user, I18n.translate("api.friends.pending.incoming"))));
				con.getRight().stream().sorted((u1, u2) -> new AlphabeticalComparator().compare(u1.getName(), u2.getName()))
					.forEach(user -> widget.addEntry(new UserListWidget.UserListEntry(user, I18n.translate("api.friends.pending.outgoing")).outgoing()));
			});
		} else if (current == Tab.BLOCKED) {
			FriendRequest.getInstance().getBlocked().whenCompleteAsync((list, th) -> widget.setUsers(list.stream().sorted((u1, u2) ->
				new AlphabeticalComparator().compare(u1.getName(), u2.getName())).collect(Collectors.toList())));
		}

		this.buttons.add(blockedTab = new ButtonWidget(9, this.width / 2 + 24, this.height - 52, 57, 20,
			I18n.translate("api.friends.tab.blocked")));

		this.buttons.add(pendingTab = new ButtonWidget(8, this.width / 2 - 34, this.height - 52, 57, 20,
			I18n.translate("api.friends.tab.pending")));

		this.buttons.add(allTab = new ButtonWidget(7, this.width / 2 - 94, this.height - 52, 57, 20,
			I18n.translate("api.friends.tab.all")));

		this.buttons.add(onlineTab = new ButtonWidget(6, this.width / 2 - 154, this.height - 52, 57, 20,
			I18n.translate("api.friends.tab.online")));

		this.buttons.add(new ButtonWidget(5, this.width / 2 + 88, this.height - 52, 66, 20,
			I18n.translate("api.friends.add")));

		this.buttons.add(removeButton = new ButtonWidget(4, this.width / 2 - 50, this.height - 28, 100, 20,
			I18n.translate("api.friends.remove")));

		buttons.add(denyButton = new ButtonWidget(3, this.width / 2 - 50, this.height - 28, 48, 20,
			I18n.translate("api.friends.request.deny")));

		buttons.add(acceptButton = new ButtonWidget(2, this.width / 2 + 2, this.height - 28, 48, 20,
			I18n.translate("api.friends.request.accept")));

		buttons.add(unblockButton = new ButtonWidget(10, this.width / 2 - 50, this.height - 28, 100, 20, I18n.translate("api.users.unblock")));
		buttons.add(cancelButton = new ButtonWidget(11, this.width / 2 - 50, this.height - 28, 100, 20, I18n.translate("gui.cancel")));

		this.buttons.add(chatButton = new ButtonWidget(1, this.width / 2 - 154, this.height - 28, 100, 20,
			I18n.translate("api.friends.chat")));

		this.buttons.add(
			new ButtonWidget(0, this.width / 2 + 4 + 50, this.height - 28, 100, 20,
				I18n.translate("gui.back")));
		updateButtonActivationStates();
	}

	private void updateButtonActivationStates() {
		UserListWidget.UserListEntry entry = widget.getSelectedEntry();
		chatButton.active = entry != null && (current == Tab.ALL || current == Tab.ONLINE);

		removeButton.visible = true;
		unblockButton.active = removeButton.active = entry != null;
		denyButton.visible = false;
		acceptButton.visible = unblockButton.visible = cancelButton.visible = false;
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

			if (entry != null && entry.isOutgoingRequest()) {
				cancelButton.visible = true;
			} else {
				denyButton.visible = true;
				acceptButton.visible = true;
			}
			denyButton.active = acceptButton.active = entry != null;
		} else if (current == Tab.BLOCKED) {
			blockedTab.active = false;
			onlineTab.active = allTab.active = pendingTab.active = true;
			removeButton.visible = false;
			unblockButton.visible = true;
		}
	}

	@Override
	public void handleMouse() {
		super.handleMouse();
		this.widget.handleMouse();
	}

	public enum Tab {
		ONLINE,
		ALL,
		PENDING,
		BLOCKED
	}
}
