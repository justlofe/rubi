package fun.lofe.amber.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiTextRenderState.class)
public interface GuiTextRenderStateAccessor {

    @Accessor("font")
    Font rubi$getFont();

    @Accessor("text")
    FormattedCharSequence rubi$getText();

    @Accessor("x")
    int rubi$getX();

    @Accessor("y")
    int rubi$getY();

    @Accessor("color")
    int rubi$getColor();

    @Accessor("backgroundColor")
    int rubi$getBackgroundColor();

    @Accessor("dropShadow")
    boolean rubi$hasDropShadow();

    @Accessor("includeEmpty")
    boolean rubi$includeEmpty();

    @Accessor("pose")
    Matrix3x2fc rubi$getPose();

    @Accessor("scissor")
    ScreenRectangle rubi$getScissor();
}