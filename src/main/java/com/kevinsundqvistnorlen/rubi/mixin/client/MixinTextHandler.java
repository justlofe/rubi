package com.kevinsundqvistnorlen.rubi.mixin.client;

import com.kevinsundqvistnorlen.rubi.IRubyStyle;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringSplitter.class)

public abstract class MixinTextHandler {

    @Unique private final ThreadLocal<Boolean> recursionGuard = ThreadLocal.withInitial(() -> false);

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    private void onTextHandlerInit(
        StringSplitter.WidthProvider widthProvider, CallbackInfo ci,
        @Local(argsOnly = true, name = "widthProvider") LocalRef<StringSplitter.WidthProvider> localWidthRetriever
    ) {
        localWidthRetriever.set((codePoint, style) -> IRubyStyle
            .getRuby(style)
            .map(rubyText -> rubyText.getWidth((StringSplitter) (Object) this))
            .orElseGet(() -> widthProvider.getWidth(codePoint, style)));
    }

    @Shadow
    public abstract float stringWidth(FormattedCharSequence text);

    @Inject(method = "stringWidth(Ljava/lang/String;)F", at = @At("HEAD"), cancellable = true, order = 900)
    private void onStringWidth(String str, CallbackInfoReturnable<Float> cir) {
        this.onStringWidth(visitor -> StringDecomposer.iterateFormatted(str, Style.EMPTY, visitor), cir);
    }

    @Inject(
        method = "stringWidth(Lnet/minecraft/network/chat/FormattedText;)F", at = @At("HEAD"), cancellable = true, order = 900
    )
    private void onStringWidth(FormattedText text, CallbackInfoReturnable<Float> cir) {
        this.onStringWidth(visitor -> StringDecomposer.iterateFormatted(text, Style.EMPTY, visitor), cir);
    }

    @Inject(method = "stringWidth(Lnet/minecraft/util/FormattedCharSequence;)F", at = @At("HEAD"), cancellable = true, order = 900)
    private void onStringWidth(FormattedCharSequence text, CallbackInfoReturnable<Float> cir) {
        if (this.recursionGuard.get()) return;
        this.recursionGuard.set(true);

        try {
            var width = new MutableFloat();
            text.accept((index, style, codePoint) -> {
                width.add(
                    IRubyStyle
                        .getRuby(style)
                        .map(ruby -> ruby.getWidth((StringSplitter) (Object) this))
                        .orElseGet(() -> this.stringWidth(FormattedCharSequence.codepoint(codePoint, style)))
                );
                return true;
            });
            cir.setReturnValue(width.floatValue());
        } finally {
            this.recursionGuard.set(false);
        }
    }
}
