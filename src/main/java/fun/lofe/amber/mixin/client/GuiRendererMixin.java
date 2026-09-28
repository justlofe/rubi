package fun.lofe.amber.mixin.client;

import fun.lofe.amber.TextPreparer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GlyphRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

    @Shadow
    @Final
    private GuiRenderState renderState;

    @Inject(
            method = "prepareText",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rubi$prepareText(CallbackInfo ci) {
        this.renderState.forEachText(this::rubi$prepareTextState);

        ci.cancel();
    }

    @Unique
    private void rubi$prepareTextState(GuiTextRenderState state) {
        GuiTextRenderStateAccessor text = (GuiTextRenderStateAccessor) (Object) state;

        Font font = text.rubi$getFont();
        FormattedCharSequence sequence = text.rubi$getText();

        float x = text.rubi$getX();
        float y = text.rubi$getY();

        int color = text.rubi$getColor();
        int backgroundColor = text.rubi$getBackgroundColor();

        boolean shadow = text.rubi$hasDropShadow();
        boolean includeEmpty = text.rubi$includeEmpty();

        Matrix3x2fc pose = state.pose;

        TextPreparer.prepare(
                sequence,
                x,
                y,
                pose,
                font.getSplitter(),
                font.lineHeight,
                (piece, pieceX, pieceY, piecePose) ->
                        rubi$drawPiece(
                                font,
                                piece,
                                pieceX,
                                pieceY,
                                color,
                                shadow,
                                includeEmpty,
                                backgroundColor,
                                piecePose,
                                state.scissor
                        )
        );
    }

    @Unique
    private void rubi$drawPiece(
            Font font, FormattedCharSequence text,
            float x, float y,
            int color, boolean shadow, boolean includeEmpty, int backgroundColor,
            Matrix3x2fc pose,
            ScreenRectangle scissor
    ) {
        Font.PreparedText prepared = font.prepareText(
                text,
                x,
                y,
                color,
                shadow,
                includeEmpty,
                backgroundColor
        );

        prepared.visit(new Font.GlyphVisitor() {
            @Override
            public void acceptRenderable(@NonNull TextRenderable renderable) {
                renderState.addGlyphToCurrentLayer(
                        new GlyphRenderState(
                                pose,
                                renderable,
                                scissor
                        )
                );
            }
        });
    }
}