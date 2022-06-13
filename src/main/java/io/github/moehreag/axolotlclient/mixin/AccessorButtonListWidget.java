package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface AccessorButtonListWidget {


	@Accessor
	<E extends EntryListWidget.Entry<E>>
	void setHoveredEntry(E hoveredEntry);
}
