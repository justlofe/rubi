package com.kevinsundqvistnorlen.rubi.mixin.client;

import com.kevinsundqvistnorlen.rubi.CompositePreparedText;
import com.kevinsundqvistnorlen.rubi.TextDrawer;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class MixinTextRenderer {

    @Unique
    private final ThreadLocal<Boolean> rubi$recursionGuard =
            ThreadLocal.withInitial(() -> false);

    @Final
    @Shadow
    public int lineHeight;

    @Final
    @Shadow
    private StringSplitter splitter;

    @Shadow
    public abstract Font.PreparedText prepareText(
            FormattedCharSequence text,
            float x,
            float y,
            int color,
            boolean drawShadow,
            boolean includeEmpty,
            int backgroundColor
    );

    @Inject(
            method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rubi$prepareText(
            FormattedCharSequence text,
            float x,
            float y,
            int originalColor,
            boolean drawShadow,
            boolean includeEmpty,
            int backgroundColor,
            CallbackInfoReturnable<Font.PreparedText> cir
    ) {
        if (rubi$recursionGuard.get()) {
            return;
        }

        rubi$recursionGuard.set(true);

        try {
            CompositePreparedText result =
                    new CompositePreparedText();

            Matrix3x2f identity = new Matrix3x2f();

            TextDrawer.draw(
                    text,
                    x,
                    y,
                    identity,
                    this.splitter,
                    this.lineHeight,

                    (part, xx, yy, _) -> {
                        Font.PreparedText prepared =
                                this.prepareText(
                                        part,
                                        xx,
                                        yy,
                                        originalColor,
                                        drawShadow,
                                        includeEmpty,
                                        backgroundColor
                                );

                        result.add(prepared);
                    }
            );

            cir.setReturnValue(result);

        } finally {
            rubi$recursionGuard.set(false);
        }
    }
}