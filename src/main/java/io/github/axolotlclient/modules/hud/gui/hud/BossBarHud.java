package io.github.axolotlclient.modules.hud.gui.hud;


import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.ColorOption;
import io.github.axolotlclient.config.options.Option;
import io.github.axolotlclient.mixin.AccessorBossBarHud;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class BossBarHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "bossbarhud");
	private static final Identifier BARS_TEXTURE = new Identifier("textures/gui/bars.png");
	private final BossBar placeholder = new CustomBossBar(Text.literal("Boss bar"), BossBar.Color.WHITE, BossBar.Style.PROGRESS);
	private final BossBar placeholder2 = Util.make(() -> {
		BossBar boss = new CustomBossBar(Text.literal("More boss bars..."), BossBar.Color.PURPLE, BossBar.Style.PROGRESS);
		boss.setPercent(0.45F);
		return boss;
	});

	private Map<UUID, ClientBossBar> bossBars;
	private final MinecraftClient client;
    private final ColorOption barColor = new ColorOption("barColor", "#FFFFFFFF");
    private final BooleanOption text = new BooleanOption("text", true);
    private final BooleanOption bar = new BooleanOption("bar", true);
    // TODO custom colour

    public BossBarHud() {
        super(186, 20);
        client = MinecraftClient.getInstance();
    }

	public void setBossBars() {
		bossBars = ((AccessorBossBarHud) client.inGameHud.getBossBarHud()).getBossBars();
	}


	@Override
	public void render(MatrixStack matrices) {
		setBossBars();
		if (this.bossBars.isEmpty()) {
			return;
		}
		matrices.push();
		scale(matrices);
		DrawPosition scaledPos = getPos();

		int by = 12;
		for (ClientBossBar bossBar : bossBars.values()) {
			renderBossBar(matrices, scaledPos.x, by + scaledPos.y, bossBar);
			by = by + 19;
			if (by > height) {
				break;
			}
		}
		matrices.pop();
	}

	@Override
	public void renderPlaceholder(MatrixStack matrices) {
		matrices.push();
		renderPlaceholderBackground(matrices);
		scale(matrices);
		DrawPosition pos = getPos();
		outlineRect(matrices, getBounds(), Color.BLACK);
		renderBossBar(matrices, pos.x, pos.y + 12, placeholder);
		renderBossBar(matrices, pos.x, pos.y + 31, placeholder2);
		hovered = false;
		matrices.pop();
	}

	private void renderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
		RenderSystem.setShaderTexture(0, BARS_TEXTURE);
		if (bar.get()) {
			DrawableHelper.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2, 182, 5, 256, 256);
			if (bossBar.getStyle() != BossBar.Style.PROGRESS) {
				DrawableHelper.drawTexture(matrices, x, y, 0, 80 + (bossBar.getStyle().ordinal() - 1) * 5 * 2, 182, 5, 256, 256);
			}

			int i = (int) (bossBar.getPercent() * 183.0F);
			if (i > 0) {
				DrawableHelper.drawTexture(matrices, x, y, 0, bossBar.getColor().ordinal() * 5 * 2 + 5, i, 5, 256, 256);
				if (bossBar.getStyle() != BossBar.Style.PROGRESS) {
					DrawableHelper.drawTexture(matrices, x, y, 0, 80 + (bossBar.getStyle().ordinal() - 1) * 5 * 2 + 5, i, 5, 256, 256);
				}
			}
		}
		if (text.get()) {
			Text text = bossBar.getName();
			float textX = x + ((float) width / 2) - ((float) client.textRenderer.getWidth(text) / 2);
			float textY = y - 9;
			if (shadow.get()) {
				client.textRenderer.drawWithShadow(matrices, text, textX, textY, textColor.get().getAsInt());
			} else {
				client.textRenderer.draw(matrices, text, textX, textY, textColor.get().getAsInt());
			}
		}
	}

	@Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(text);
        options.add(textColor);
        options.add(shadow);
        options.add(bar);
        options.add(barColor);
    }

	public static class CustomBossBar extends BossBar {
		public CustomBossBar(Text name, Color color, Style style) {
			super(MathHelper.randomUuid(), name, color, style);
		}
	}
}
