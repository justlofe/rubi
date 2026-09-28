package fun.lofe.amber.mixin.client;

import fun.lofe.amber.RubyText;
import fun.lofe.amber.ruby.RubyStyle;
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
public abstract class StringSplitterMixin {

    @Unique
    private final ThreadLocal<Boolean> recursionGuard = ThreadLocal.withInitial(() -> false);

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    private void onTextHandlerInit(
            StringSplitter.WidthProvider widthProvider, CallbackInfo ci,
            @Local(argsOnly = true, name = "widthProvider") LocalRef<StringSplitter.WidthProvider> localWidthRetriever
    ) {
        localWidthRetriever.set((codepoint, style) -> {
            RubyText rubyText = RubyStyle.getRubyText(style);
            if(rubyText == null) return widthProvider.getWidth(codepoint, style);

            return rubyText.getWidth((StringSplitter) (Object) this);
        });
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
            text.accept((_, style, codePoint) -> {
                RubyText rubyText = RubyStyle.getRubyText(style);
                if(rubyText == null) {
                    width.add(this.stringWidth(FormattedCharSequence.codepoint(codePoint, style)));
                    return true;
                }

                width.add(rubyText.getWidth((StringSplitter) (Object) this));
                return true;
            });
            cir.setReturnValue(width.floatValue());
        } finally {
            this.recursionGuard.set(false);
        }
    }
}
