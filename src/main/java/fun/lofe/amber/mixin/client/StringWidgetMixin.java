package fun.lofe.amber.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StringWidget.class)
public abstract class StringWidgetMixin extends AbstractStringWidget {

    public StringWidgetMixin(int x, int y, int width, int height, Component message, Font textRenderer) {
        super(x, y, width, height, message, textRenderer);
    }

    @Unique
    public int getWidth() {
        return this.getFont().width(this.getMessage().getVisualOrderText());
    }

}
