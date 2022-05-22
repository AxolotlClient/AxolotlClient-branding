package io.github.moehreag.axolotlclient.modules.motionblur;

import com.google.gson.JsonSyntaxException;
import io.github.moehreag.axolotlclient.AxolotlClient;
import io.github.moehreag.axolotlclient.config.AxolotlClientConfig;
import io.github.moehreag.axolotlclient.config.options.FloatOption;
import io.github.moehreag.axolotlclient.mixin.AccessorShaderEffect;
import io.github.moehreag.axolotlclient.modules.AbstractModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.resource.ResourceMetadataProvider;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class MotionBlur extends AbstractModule {

    public static Identifier ID = new Identifier("motion_blur");

    private Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");

    public ShaderEffect shader;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float currentBlur;

    private int lastWidth;

    private int lastHeight;

    @Override
    public void init() {
        AxolotlClient.runtimeResources.put(shaderLocation, new MotionBlurShader());
    }

    public void onUpdate() {
        if(shader == null || client.width!=lastWidth || client.height!=lastHeight) {
            currentBlur=getBlur();
            try {
                shader = new ShaderEffect(client.getTextureManager(),
                        client.getResourceManager(), client.getFramebuffer(),
                        shaderLocation);
                shader.setupDimensions(client.width, client.height);
            } catch (JsonSyntaxException | IOException e) {
                AxolotlClient.LOGGER.error("Could not load motion blur", e);
            }
        }
        if(currentBlur!=getBlur()){
            ((AccessorShaderEffect)shader).getPasses().forEach(shader -> {
                GlUniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if(blendFactor!=null){
                    blendFactor.method_6976(getBlur());
                }
            });
            currentBlur=getBlur();
        }

        lastWidth = client.width;
        lastHeight = client.height;
    }

    private static float getBlur() {
        return AxolotlClient.CONFIG.motionBlurStrength.get();
    }

    public class MotionBlurShader implements Resource {

        @Override
        public Identifier getId() {
            return null;
        }

        @Override
        public InputStream getInputStream() {
            return IOUtils.toInputStream(String.format("{" +
                    "    \"targets\": [" +
                    "        \"swap\"," +
                    "        \"previous\"" +
                    "    ]," +
                    "    \"passes\": [" +
                    "        {" +
                    "            \"name\": \"motion_blur\"," +
                    "            \"intarget\": \"minecraft:main\"," +
                    "            \"outtarget\": \"swap\"," +
                    "            \"auxtargets\": [" +
                    "                {" +
                    "                    \"name\": \"PrevSampler\"," +
                    "                    \"id\": \"previous\"" +
                    "                }" +
                    "            ]," +
                    "            \"uniforms\": [" +
                    "                {" +
                    "                    \"name\": \"BlendFactor\"," +
                    "                    \"values\": [ %s ]" +
                    "                }" +
                    "            ]" +
                    "        }," +
                    "        {" +
                    "            \"name\": \"blit\"," +
                    "            \"intarget\": \"swap\"," +
                    "            \"outtarget\": \"previous\"" +
                    "        }," +
                    "        {" +
                    "            \"name\": \"blit\"," +
                    "            \"intarget\": \"swap\"," +
                    "            \"outtarget\": \"minecraft:main\"" +
                    "        }" +
                    "    ]" +
                    "}", getBlur()));
        }

        @Override
        public boolean hasMetadata() {
            return false;
        }

        @Override
        public <T extends ResourceMetadataProvider> T getMetadata(String key) {
            return null;
        }

        @Override
        public String getResourcePackName() {
            return null;
        }
    }


}
