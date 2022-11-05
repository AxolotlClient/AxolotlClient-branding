package io.github.axolotlclient.modules.hud.gui.hud.item;

import io.github.axolotlclient.AxolotlclientConfig.options.BooleanOption;
import io.github.axolotlclient.AxolotlclientConfig.options.Option;
import io.github.axolotlclient.modules.hud.gui.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */

public class ArmorHud extends TextHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "armorhud");

    protected BooleanOption showProtLvl = new BooleanOption("showProtectionLevel", false);

    public ArmorHud() {
        super(20, 100, true);
    }

    @Override
    public void renderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        renderMainItem(matrices, client.player.getInventory().getMainHandStack(), pos.x() + 2, pos.y() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack stack = client.player.getInventory().getArmorStack(i).copy();
            if (showProtLvl.get() && stack.hasEnchantments()) {
                NbtList nbtList = stack.getEnchantments();
                if (nbtList != null) {
                    for (int k = 0; k < nbtList.size(); ++k) {
                        int enchantId = nbtList.getCompound(k).getShort("id");
                        int level = nbtList.getCompound(k).getShort("lvl");
                        if (enchantId == 0 && Enchantment.byRawId(enchantId) != null) {
                            stack.setCount(level);
                        }
                    }
                }
            }
            renderItem(matrices, stack, pos.x() + 2, lastY + pos.y());
            lastY = lastY - 20;
        }
    }

    public void renderItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(getScale(), stack, x, y);
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, null, textColor.get().getAsInt(), shadow.get());
    }

    public void renderMainItem(MatrixStack matrices, ItemStack stack, int x, int y) {
        ItemUtil.renderGuiItemModel(getScale(), stack, x, y);
        String total = String.valueOf(ItemUtil.getTotal(client, stack));
        if (total.equals("1")) {
            total = null;
        }
        ItemUtil.renderGuiItemOverlay(matrices, client.textRenderer, stack, x, y, total, textColor.get().getAsInt(), shadow.get());
    }

    private final ItemStack[] placeholderStacks = new ItemStack[]{
            new ItemStack(Items.IRON_BOOTS),
            new ItemStack(Items.IRON_LEGGINGS),
            new ItemStack(Items.IRON_CHESTPLATE),
            new ItemStack(Items.IRON_HELMET),
            new ItemStack(Items.IRON_SWORD)
    };

    @Override
    public void renderPlaceholderComponent(MatrixStack matrices, float delta) {
        DrawPosition pos = getPos();
        int lastY = 2 + (4 * 20);
        renderItem(matrices, placeholderStacks[4], pos.x() + 2, pos.y() + lastY);
        lastY = lastY - 20;
        for (int i = 0; i <= 3; i++) {
            ItemStack item = placeholderStacks[i];
            renderItem(matrices, item, pos.x() + 2, lastY + pos.y());
            lastY = lastY - 20;
        }
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
    public List<Option<?>> getConfigurationOptions() {
        List<Option<?>> options = super.getConfigurationOptions();
        options.add(showProtLvl);
        return options;
    }

}
