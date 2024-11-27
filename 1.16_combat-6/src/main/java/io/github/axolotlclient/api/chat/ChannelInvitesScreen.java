package io.github.axolotlclient.api.chat;

import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.PlainTextButtonWidget;
import io.github.axolotlclient.api.requests.ChannelRequest;
import io.github.axolotlclient.api.types.ChannelInvite;
import io.github.axolotlclient.api.util.UUIDHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

public class ChannelInvitesScreen extends Screen {
    private final Screen parent;
    private ButtonWidget acceptButton;
    private ButtonWidget denyButton;
    private InvitesListWidget invites;

    public ChannelInvitesScreen(Screen parent) {
        super(new TranslatableText("api.channels.invites"));
        this.parent = parent;
    }

    @Override
    protected void init() {

        addButton(new PlainTextButtonWidget(width / 2 - textRenderer.getWidth(title)/2, 33 / 2, 0, 0, title, w -> {
        }, textRenderer));

        invites = addChild(new InvitesListWidget(client, height, 33, width, height - 88, 25));


        acceptButton = addButton(new ButtonWidget(width / 2 - 75, height - 55 / 2 - 2 - 20, 73, 20, new TranslatableText("api.channels.invite.accept"), w -> {
            if (invites.getSelected() != null) {
                ChannelRequest.acceptChannelInvite(invites.getSelected().invite);
                init(client, width, height);
            }
        }));
        denyButton = addButton(new ButtonWidget(width / 2 + 2, height - 55 / 2 - 2 - 20, 73, 20, new TranslatableText("api.channels.invite.ignore"), w -> {
            if (invites.getSelected() != null) {
                ChannelRequest.ignoreChannelInvite(invites.getSelected().invite);
                init(client, width, height);
            }
        }));
        addButton(new ButtonWidget(width / 2 - 75, height - 55 / 2 + 2, 150, 20, ScreenTexts.BACK, w -> onClose()));

        updateButtons();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        invites.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        client.openScreen(parent);
    }

    private void updateButtons() {
        denyButton.active = acceptButton.active = invites.getSelected() != null;
    }

    private class InvitesListWidget extends AlwaysSelectedEntryListWidget<InvitesListWidget.InvitesListEntry> {

        public InvitesListWidget(MinecraftClient client, int screenHeight, int y, int width, int height, int entryHeight) {
            super(client, width, screenHeight, y, y + height, entryHeight);
            ChannelRequest.getChannelInvites().thenAccept(list ->
                    list.stream().map(InvitesListEntry::new).forEach(this::addEntry));
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean bl = super.mouseClicked(mouseX, mouseY, button);
            updateButtons();
            return bl;
        }

        private class InvitesListEntry extends Entry<InvitesListEntry> {

            private final ChannelInvite invite;

            public InvitesListEntry(ChannelInvite invite) {
                this.invite = invite;
            }

            @Override
            public void render(MatrixStack graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
                drawTextWithShadow(graphics, textRenderer, new TranslatableText("api.channels.invite.name", invite.channelName()), left + 2, top + 2, -1);
                drawTextWithShadow(graphics, textRenderer, new TranslatableText("api.channels.invite.from", UUIDHelper.getUsername(invite.fromUuid())).setStyle(Style.EMPTY.withItalic(true)), left + 15, top + height - textRenderer.fontHeight - 1, 0x808080);

            }
        }
    }
}
