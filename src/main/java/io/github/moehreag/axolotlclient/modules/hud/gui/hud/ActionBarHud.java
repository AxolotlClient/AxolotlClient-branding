package io.github.moehreag.axolotlclient.modules.hud.gui.hud;


import io.github.moehreag.axolotlclient.modules.hud.gui.AbstractHudEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ActionBarHud extends AbstractHudEntry {

    public static final Identifier ID = new Identifier("kronhud", "actionbarhud");

    //public KronInteger timeShown = new KronInteger("timeshown", ID.getPath(), 60, 40, 300);

    /*private Text actionBar;
    private int ticksShown;
    private int color;
    private final String placeholder = "Action Bar";*/
    MinecraftClient client;

    public ActionBarHud() {
        super(115, 13);
        client = MinecraftClient.getInstance();
    }

    @Override
    public void render() {

    }

    @Override
    public void renderPlaceholder() {

    }

    /*public void setActionBar(Text bar, int color){this.actionBar = bar; this.color = color;}

    @Override
    public void render(MatrixStack matrices) {
        if (ticksShown >= timeShown.getIntegerValue()){
            this.actionBar = null;
        }
        if(this.actionBar != null) {

            matrices.push();
            scale(matrices);
            if (shadow.getBooleanValue()){
                client.textRenderer.drawWithShadow(matrices, actionBar, (float)getPos().x() + Math.round((float) width /2) -  (float) client.textRenderer.getWidth(actionBar) /2, (float)getPos().y() + 3, color);
            } else {

                client.textRenderer.draw(matrices, actionBar, (float)getPos().x() + Math.round((float) width /2) - ((float) client.textRenderer.getWidth(actionBar) /2), (float)getPos().y() + 3, color);
            }
            matrices.pop();
            ticksShown++;
        } else {
            ticksShown = 0;
        }
    }

    @Override
    public void renderPlaceholder(MatrixStack matrices) {
        matrices.push();
        renderPlaceholderBackground(matrices);
        scale(matrices);
        client.textRenderer.draw(matrices, placeholder,  (float)getPos().x() + Math.round((float) width /2) - (float) client.textRenderer.getWidth(placeholder) /2, (float)getPos().y() + 3, textColor.getColor().color());
        matrices.pop();
        hovered = false;
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
    public void addConfigOptions(List<IConfigBase> options){
        super.addConfigOptions(options);
        options.add(shadow);
        options.add(timeShown);
    }*/
}
