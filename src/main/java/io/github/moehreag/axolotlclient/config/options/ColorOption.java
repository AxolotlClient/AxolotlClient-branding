package io.github.moehreag.axolotlclient.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.moehreag.axolotlclient.config.Color;
import io.github.moehreag.axolotlclient.config.screen.widgets.ColorOptionWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import org.jetbrains.annotations.NotNull;

public class ColorOption extends OptionBase implements Option {

    private final Color def;
    private Color value;

    public ColorOption(String name, String def){
        this(name, Color.parse(def));
    }

    public ColorOption(String name, Color def){
        super(name);
        this.def=def;
    }

    public Color get(){
        return value;
    }
    public void set(Color set){this.value=set;}

    @Override
    public OptionType getType() {
        return OptionType.COLOR;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setValueFromJsonElement(@NotNull JsonElement element) {
        value=Color.parse(element.getAsString());
    }

    @Override
    public void setDefaults() {
        value=def;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(String.valueOf(value));
    }

    public Color getChroma(){
        return Color.getChroma();
    }

}
