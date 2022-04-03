package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ArmorHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "armorhud");

    public ArmorHud() {
        super(20, 100);
    }

    @Override
    public void render() {
        /*matrices.push();
        scale(matrices);
        DrawPosition pos = getPos();
        if (background.getBooleanValue()) {
            fillRect(matrices, getBounds(),
                    backgroundColor.getColor());
        }
        int lastY = 2 + (4 * 20);
        renderMainItem(matrices, client.player.getInventory().getMainHandStack(), pos.x() + 2, pos.y() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack item = client.player.getInventory().armor.get(i);
            renderItem(matrices, item, pos.x() + 2, lastY + pos.y());
            lastY = lastY - 20;
        }
        matrices.pop();*/
    }

    /*public void renderItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(matrices, stack, x, y);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, null, textColor.getColor().color(),
                shadow.getBooleanValue());
    }

    public void renderMainItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(matrices, stack, x, y);
        String total = String.valueOf(ItemUtil.getTotal(client, stack));
        if (total.equals("1")) {
            total = null;
        }
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y,
                total, textColor.getColor().color(),
                shadow.getBooleanValue());
    }*/

    @Override
    public void renderPlaceholder() {
        /*matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        ItemUtil.renderGuiItemModel(matrices, new ItemStack(Items.GRASS_BLOCK), pos.x() + 2, pos.y() + lastY);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, new ItemStack(Items.GRASS_BLOCK), pos.x() + 2,
                pos.y() + lastY, "90", textColor.getColor().color(), shadow.getBooleanValue());
        hovered = false;
        matrices.pop();*/
    }

    @Override
    public boolean movable() {
        return true;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        //options.add(textColor);
        options.add(shadow);
        options.add(background);
        //options.add(backgroundColor);
    }

}
