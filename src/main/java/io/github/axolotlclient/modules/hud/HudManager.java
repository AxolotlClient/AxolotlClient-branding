package io.github.axolotlclient.modules.hud;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlclientConfig.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.hud.gui.AbstractHudEntry;
import io.github.axolotlclient.modules.hud.gui.hud.*;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 */


public class HudManager extends AbstractModule {

    private final Map<Identifier, AbstractHudEntry> entries = new LinkedHashMap<>();

    private final OptionCategory hudCategory = new OptionCategory("hud", false);

    private final MinecraftClient client = MinecraftClient.getInstance();
    private static final HudManager INSTANCE = new HudManager();

    static KeyBind key = new KeyBind("key.openHud", InputUtil.KEY_RIGHT_SHIFT_CODE, "category.axolotlclient");

    public static HudManager getInstance(){
        return INSTANCE;
    }

    public void init(){

        KeyBindingHelper.registerKeyBinding(key);

        AxolotlClient.CONFIG.addCategory(hudCategory);

        HudRenderCallback.EVENT.register((matrices, v)->render(matrices));

        add(new PingHud());
        add(new FPSHud());
        add(new CPSHud());
        add(new ArmorHud());
        add(new PotionsHud());
        add(new KeystrokeHud());
        add(new ToggleSprintHud());
        add(new IPHud());
        add(new iconHud());
        add(new SpeedHud());
        add(new ScoreboardHud());
        add(new CrosshairHud());
        add(new CoordsHud());
        add(new ActionBarHud());
        add(new BossBarHud());
        add(new ArrowHud());
        add(new ItemUpdateHud());
        add(new PackDisplayHud());
        add(new RealTimeHud());
        add(new ReachDisplayHud());
        add(new HotbarHUD());
        add(new MemoryHud());
        add(new PlayerCountHud());

        entries.forEach((identifier, abstractHudEntry) -> abstractHudEntry.init());
    }

    public void tick(){
        if(key.isPressed()) MinecraftClient.getInstance().setScreen(new HudEditScreen());
        INSTANCE.entries.forEach((identifier, abstractHudEntry) -> {
            if(abstractHudEntry.isEnabled() && abstractHudEntry.tickable())abstractHudEntry.tick();
        });
    }

    public HudManager add(AbstractHudEntry entry) {
        entries.put(entry.getId(), entry);
        hudCategory.addSubCategory(entry.getAllOptions());
        return this;
    }

    public List<AbstractHudEntry> getEntries() {
        if (entries.size() > 0) {
            return new ArrayList<>(entries.values());
        }
        return new ArrayList<>();
    }

    public List<AbstractHudEntry> getMovableEntries() {
        if (entries.size() > 0) {
            return entries.values().stream().filter((entry) -> entry.isEnabled() && entry.movable()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public AbstractHudEntry get(Identifier identifier) {
        return entries.get(identifier);
    }

    public void render(MatrixStack matrices) {
        client.getProfiler().push("Hud Modules");
        if (!(client.currentScreen instanceof HudEditScreen) && !client.options.debugEnabled) {
            for (AbstractHudEntry hud : getEntries()) {
                if (hud.isEnabled()) {
                    client.getProfiler().push(hud.getName());
                    hud.renderHud(matrices);
                    client.getProfiler().pop();
                }
            }
        }
        client.getProfiler().pop();
    }

    public Optional<AbstractHudEntry> getEntryXY(int x, int y) {
        for (AbstractHudEntry entry : getMovableEntries()) {
            Rectangle bounds = entry.getScaledBounds();
            if (bounds.x <= x && bounds.x + bounds.width >= x && bounds.y <= y && bounds.y + bounds.height >= y) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public void renderPlaceholder(MatrixStack matrices) {
        for (AbstractHudEntry hud : getEntries()) {
            if (hud.isEnabled()) {
                hud.renderPlaceholder(matrices);
            }
        }
    }

    public List<Rectangle> getAllBounds() {
        ArrayList<Rectangle> bounds = new ArrayList<>();
        for (AbstractHudEntry entry : getMovableEntries()) {
            bounds.add(entry.getScaledBounds());
        }
        return bounds;
    }
}
