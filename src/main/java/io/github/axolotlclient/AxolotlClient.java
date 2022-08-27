package io.github.axolotlclient;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.config.AxolotlClientConfig;
import io.github.axolotlclient.config.Color;
import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.options.BooleanOption;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.modules.freelook.Freelook;
import io.github.axolotlclient.modules.hud.HudManager;
import io.github.axolotlclient.modules.hypixel.HypixelMods;
import io.github.axolotlclient.modules.hypixel.nickhider.NickHider;
import io.github.axolotlclient.modules.motionblur.MotionBlur;
import io.github.axolotlclient.modules.particles.Particles;
import io.github.axolotlclient.modules.rpc.DiscordRPC;
import io.github.axolotlclient.modules.scrollableTooltips.ScrollableTooltips;
import io.github.axolotlclient.modules.tnttime.TntTime;
import io.github.axolotlclient.modules.zoom.Zoom;
import io.github.axolotlclient.util.UnsupportedMod;
import io.github.axolotlclient.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class AxolotlClient implements ClientModInitializer {

	public static Logger LOGGER = LogManager.getLogger("AxolotlClient");

	public static AxolotlClientConfig CONFIG;
	public static String onlinePlayers = "";
	public static String otherPlayers = "";

	public static List<ResourcePack> packs = new ArrayList<>();
	public static HashMap<Identifier, Resource> runtimeResources = new HashMap<>();

	public static final Identifier badgeIcon = new Identifier("axolotlclient", "textures/badge.png");

	public static final OptionCategory config = new OptionCategory("storedOptions");
	public static final BooleanOption someNiceBackground = new BooleanOption("defNoSecret", false);
	public static final List<AbstractModule> modules= new ArrayList<>();

	public static Integer tickTime = 0;

    public static UnsupportedMod badmod;
    public static boolean titleDisclaimer = false;
    public static boolean showWarning = true;

    @Override
    public void onInitializeClient() {

        if (FabricLoader.getInstance().isModLoaded("ares")){
            badmod = new UnsupportedMod("Ares Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
        } else if (FabricLoader.getInstance().isModLoaded("inertia")) {
            badmod = new UnsupportedMod("Inertia Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
        } else if (FabricLoader.getInstance().isModLoaded("meteor-client")) {
            badmod = new UnsupportedMod("Meteor Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
        } else if (FabricLoader.getInstance().isModLoaded("wurst")) {
            badmod = new UnsupportedMod("Wurst Client", UnsupportedMod.UnsupportedReason.BAN_REASON);
        } else if (FabricLoader.getInstance().isModLoaded("baritone")) {
            badmod = new UnsupportedMod("Baritone", UnsupportedMod.UnsupportedReason.BAN_REASON);
        } else if (FabricLoader.getInstance().isModLoaded("xaerominimap")) {
            badmod = new UnsupportedMod("Xaero's Minimap", UnsupportedMod.UnsupportedReason.UNKNOWN_CONSEQUENSES);
        } else if (FabricLoader.getInstance().isModLoaded("essential-container")){
            badmod = new UnsupportedMod("Essential", UnsupportedMod.UnsupportedReason.MIGHT_CRASH, UnsupportedMod.UnsupportedReason.UNKNOWN_CONSEQUENSES);
        } else {
            showWarning = false;
        }

		CONFIG = new AxolotlClientConfig();
		config.add(someNiceBackground);

		getModules();
		CONFIG.init();
		modules.forEach(AbstractModule::init);

		CONFIG.config.addAll(CONFIG.getCategories());
		CONFIG.config.add(config);

		ConfigManager.load();

		modules.forEach(AbstractModule::lateInit);

        FabricLoader.getInstance().getModContainer("axolotlclient").ifPresent(container ->
		    ResourceManagerHelper.registerBuiltinResourcePack(
                new Identifier("axolotlclient", "axolotlclient-ui"),
                container, ResourcePackActivationType.NORMAL)
        );
		ClientTickEvents.END_CLIENT_TICK.register(client -> tickClient());

		LOGGER.info("AxolotlClient Initialized");
	}

	public static void getModules(){
		modules.add(Zoom.getInstance());
		modules.add(HudManager.getInstance());
		modules.add(HypixelMods.getInstance());
		modules.add(MotionBlur.getInstance());
        modules.add(ScrollableTooltips.getInstance());
        modules.add(DiscordRPC.getInstance());
        modules.add(Freelook.getInstance());
        modules.add(TntTime.getInstance());
        modules.add(Particles.getInstance());
	}

	public static boolean isUsingClient(UUID uuid){
		assert MinecraftClient.getInstance().player != null;
		if (uuid == MinecraftClient.getInstance().player.getUuid()){
			return true;
		} else {
			return NetworkHelper.getOnline(uuid);
		}
	}


	public static void tickClient(){

        modules.forEach(AbstractModule::tick);
		Color.tickChroma();

		if (tickTime >=6000){

			//System.out.println("Cleared Cache of Other Players!");
			otherPlayers = "";
			tickTime = 0;
		}
		tickTime++;

	}

	public static void addBadge(Entity entity, MatrixStack matrices){
		if(entity instanceof PlayerEntity && !entity.isSneaky()){

			if(AxolotlClient.CONFIG.showBadges.get() && AxolotlClient.isUsingClient(entity.getUuid())) {
                RenderSystem.enableDepthTest();
                MinecraftClient.getInstance().getTextureManager().bindTexture(badgeIcon);

				int x = -(MinecraftClient.getInstance().textRenderer.getWidth(
						entity.getUuid() == MinecraftClient.getInstance().player.getUuid()?
						(NickHider.Instance.hideOwnName.get() ? NickHider.Instance.hiddenNameSelf.get(): Team.modifyText(entity.getScoreboardTeam(), entity.getName()).getString()):
						(NickHider.Instance.hideOtherNames.get() ? NickHider.Instance.hiddenNameOthers.get(): Team.modifyText(entity.getScoreboardTeam(), entity.getName()).getString())
				)/2 + (AxolotlClient.CONFIG.customBadge.get() ? MinecraftClient.getInstance().textRenderer.getWidth(" "+Formatting.strip(AxolotlClient.CONFIG.badgeText.get())): 10));

				RenderSystem.color4f(1, 1, 1, 1);

				if(AxolotlClient.CONFIG.customBadge.get()) {
                    LiteralText badgeText = Util.formatFromCodes(AxolotlClient.CONFIG.badgeText.get());
					if(AxolotlClient.CONFIG.useShadows.get()) {
						MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, badgeText, x, 0, -1);
					} else {
						MinecraftClient.getInstance().textRenderer.draw(matrices, badgeText, x, 0, -1);
					}
				}
				else DrawableHelper.drawTexture(matrices, x, 0, 0, 0, 8, 8, 8, 8);
			}
		}
	}
}
