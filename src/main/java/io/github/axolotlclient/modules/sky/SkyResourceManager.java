package io.github.axolotlclient.modules.sky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.modules.AbstractModule;
import io.github.axolotlclient.util.Logger;
import io.github.moehreag.searchInResources.SearchableResourceManager;
import net.legacyfabric.fabric.api.resource.IdentifiableResourceReloadListener;
import net.legacyfabric.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This implementation of custom skies is based on the FabricSkyBoxes mod by AMereBagatelle
 * <a href="https://github.com/AMereBagatelle/FabricSkyBoxes">Github Link.</a>
 * @license MIT
 **/

public class SkyResourceManager extends AbstractModule implements IdentifiableResourceReloadListener {

    private static final SkyResourceManager Instance = new SkyResourceManager();

    public static SkyResourceManager getInstance(){
        return Instance;
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private void loadMCPSky(String loader, Identifier id, Resource resource){
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

        JsonObject object = new JsonObject();
        String string;
        String[] option;
        try {

            while ((string = reader.readLine()) != null) {
                try {

                    if(string.startsWith("#")){
                        continue;
                    }
                    option = string.split("=");
                    if (option[0].equals("source")) {
                        if(option[1].startsWith("assets")){
                            option[1] = option[1].replace("./", "").replace("assets/minecraft/", "");
                        } else {
                            if(id.getPath().contains("world")) {
                                option[1] = loader + "/sky/world" + id.getPath().split("world")[1].split("/")[0] + "/" + option[1].replace("./", "");
                            }
                        }
                    }
                    if (option[0].equals("startFadeIn") || option[0].equals("endFadeIn") || option[0].equals("startFadeOut") || option[0].equals("endFadeOut")) {
                        option[1] = option[1].replace(":", "").replace("\\", "");
                    }

                    object.addProperty(option[0], option[1]);

                } catch (Exception ignored) {}
            }

            if (!object.get("source").getAsString().contains("sunflare"))
                SkyboxManager.getInstance().addSkybox(new MCPSkyboxInstance(object));


        } catch (Exception ignored){}
    }

    @Override
    public net.legacyfabric.fabric.api.util.Identifier getFabricId() {
        return new net.legacyfabric.fabric.api.util.Identifier(AxolotlClient.modid);
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        SkyboxManager.getInstance().clearSkyboxes();
        for (Map.Entry<Identifier, Resource> entry : ((SearchableResourceManager)resourceManager).findResources("fabricskyboxes","sky", identifier -> identifier.getPath().endsWith(".json")).entrySet()){
            Logger.debug("Loaded sky: "+entry.getKey());
            SkyboxManager.getInstance().addSkybox(new FSBSkyboxInstance(gson.fromJson(new BufferedReader(new InputStreamReader(
                    entry.getValue().getInputStream(),
                            StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")),
                    JsonObject.class)));
        }

        for (Map.Entry<Identifier, Resource> entry : ((SearchableResourceManager) resourceManager).findResources("minecraft", "optifine/sky", identifier -> identifier.getPath().endsWith(".properties")).entrySet()) {
            Logger.debug("Loaded sky: " + entry.getKey());
            loadMCPSky("optifine", entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Identifier, Resource> entry : ((SearchableResourceManager) resourceManager).findResources("minecraft", "mcpatcher/sky", identifier -> identifier.getPath().endsWith(".properties")).entrySet()) {
            Logger.debug("Loaded sky: " + entry.getKey());
            loadMCPSky("mcpatcher", entry.getKey(), entry.getValue());
        }

        AxolotlClient.initalized = true;
    }

    @Override
    public void init() {
        ResourceManagerHelper.getInstance().registerReloadListener(this);
    }
}
