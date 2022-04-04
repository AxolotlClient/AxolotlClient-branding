package io.github.moehreag.axolotlclient.modules.hud.gui.hud;

import io.github.moehreag.axolotlclient.config.options.Option;
import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.moehreag.axolotlclient.modules.hud.util.ItemUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of Hud modules is based on KronHUD.
 * https://github.com/DarkKronicle/KronHUD
 * Licensed under GPL-3.0
 */

public class ItemUpdateHud extends AbstractHudEntry {
    public static final Identifier ID = new Identifier("kronhud", "itemupdatehud");

    /*private List<ItemUtil.ItemStorage> oldItems = new ArrayList<>();
    private ArrayList<ItemUtil.TimedItemStorage> removed;
    private ArrayList<ItemUtil.TimedItemStorage> added;*/

    //private KronInteger timeout = new KronInteger("timeout", ID.getPath(), 6, 1, 60);

    public ItemUpdateHud() {
        super(200, 11 * 6 - 2);
        //removed = new ArrayList<>();
        //added = new ArrayList<>();
    }

    public void update() {
        /*this.removed = ItemUtil.removeOld(removed, timeout.getIntegerValue() * 1000);
        this.added = ItemUtil.removeOld(added, timeout.getIntegerValue() * 1000);
        updateAdded();
        updateRemoved();
        oldItems = ItemUtil.storageFromItem(ItemUtil.getItems(client));*/
    }

    @Override
    public boolean tickable() {
        return true;
    }

    @Override
    public void tick() {
        if (client.world != null) {
            update();
        }
    }

    /*private void updateAdded() {
        List<ItemUtil.ItemStorage> added = ItemUtil.compare(ItemUtil.storageFromItem(ItemUtil.getItems(client)), oldItems);
        ArrayList<ItemUtil.TimedItemStorage> timedAdded = new ArrayList<>();
        for (ItemUtil.ItemStorage stack : added) {
            timedAdded.add(stack.timed());
        }
        for (ItemUtil.TimedItemStorage stack : timedAdded) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = ItemUtil.getTimedItemFromItem(stack.stack, this.added);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.added.add(stack);
            }
        }
        this.added.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }*/

    /*private void updateRemoved() {
        List<ItemUtil.ItemStorage> removed = ItemUtil.compare(oldItems, ItemUtil.storageFromItem(ItemUtil.getItems(client)));
        List<ItemUtil.TimedItemStorage> timed = ItemUtil.untimedToTimed(removed);
        for (ItemUtil.TimedItemStorage stack : timed) {
            if (stack.stack.isEmpty()) {
                continue;
            }
            Optional<ItemUtil.TimedItemStorage> item = ItemUtil.getTimedItemFromItem(stack.stack, this.removed);
            if (item.isPresent()) {
                item.get().incrementTimes(stack.times);
            } else {
                this.removed.add(stack);
            }
        }
        this.removed.sort((o1, o2) -> Float.compare(o1.getPassedTime(), o2.getPassedTime()));
    }*/

    @Override
    public void render() {
        /*matrices.push();
        scale(matrices);
        DrawPosition pos = getPos();
        int lastY = 1;
        int i = 0;
        for (ItemUtil.ItemStorage item : this.added) {
            if (i > 5) {
                matrices.pop();
                return;
            }
            TextCollector message = new TextCollector();
            message.add(new LiteralText("+ "));
            message.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
            message.add(new LiteralText(item.times + "").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            message.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
            message.add(item.stack.getName());
            OrderedText text = Language.getInstance().reorder(message.getCombined());
            if (shadow.getBooleanValue()) {
                client.textRenderer.drawWithShadow(matrices, text, pos.x(), pos.y() + lastY,
                        Color.SELECTOR_GREEN.color());
            } else {
                client.textRenderer.draw(matrices, text, pos.x(), pos.y() + lastY,
                        Color.SELECTOR_GREEN.color());
            }
            lastY = lastY + client.textRenderer.fontHeight + 2;
            i++;
        }
        for (ItemUtil.ItemStorage item : this.removed) {
            if (i > 5) {
                matrices.pop();
                return;
            }
            TextCollector message = new TextCollector();
            message.add(new LiteralText("- "));
            message.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
            message.add(new LiteralText(item.times + "").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
            message.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
            message.add(item.stack.getName());
            OrderedText text = Language.getInstance().reorder(message.getCombined());
            if (shadow.getBooleanValue()) {
                client.textRenderer.drawWithShadow(matrices, text, pos.x(), pos.y() + lastY,
                        Formatting.RED.getColorValue());
            } else {
                client.textRenderer.draw(matrices, text, pos.x(), pos.y() + lastY,
                        Formatting.RED.getColorValue());
            }
            lastY = lastY + client.textRenderer.fontHeight + 2;
            i++;
        }
        matrices.pop();*/
    }

    @Override
    public void renderPlaceholder() {
        /*matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        DrawPosition pos = getPos();
        TextCollector addM = new TextCollector();
        addM.add(new LiteralText("+ "));
        addM.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
        addM.add(new LiteralText("2").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        addM.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
        addM.add(new ItemStack(Items.DIRT).getName());
        OrderedText addText = Language.getInstance().reorder(addM.getCombined());
        if (shadow.getBooleanValue()) {
            client.textRenderer.drawWithShadow(matrices, addText, pos.x(), pos.y(),
                    Formatting.RED.getColorValue());
        } else {
            client.textRenderer.draw(matrices, addText, pos.x(), pos.y() + client.textRenderer.fontHeight + 2,
                    Formatting.RED.getColorValue());
        }
        TextCollector removeM = new TextCollector();
        removeM.add(new LiteralText("- "));
        removeM.add(new LiteralText("[").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
        removeM.add(new LiteralText("4").setStyle(Style.EMPTY.withColor(Formatting.WHITE)));
        removeM.add(new LiteralText("] ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.DARK_GRAY.color()))));
        removeM.add(new ItemStack(Items.GRASS).getName());
        OrderedText removeText = Language.getInstance().reorder(removeM.getCombined());
        if (shadow.getBooleanValue()) {
            client.textRenderer.drawWithShadow(matrices, removeText, pos.x(), pos.y() + client.textRenderer.fontHeight + 2,
                    Formatting.RED.getColorValue());
        } else {
            client.textRenderer.draw(matrices, removeText, pos.x(), pos.y() + client.textRenderer.fontHeight + 3,
                    Formatting.RED.getColorValue());
        }
        hovered = false;
        matrices.pop();*/
    }

    @Override
    public void addConfigOptions(List<Option> options) {
        super.addConfigOptions(options);
        options.add(shadow);
        //options.add(timeout);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public boolean movable() {
        return true;
    }

}

