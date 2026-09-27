package com.kevinsundqvistnorlen.rubi.mixin.client;

import com.kevinsundqvistnorlen.rubi.IRubyStyle;
import com.kevinsundqvistnorlen.rubi.RubyText;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringDecomposer.class)
public abstract class MixinTextVisitFactory {

    @Inject(
            method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/ChatFormatting;getByCode(C)Lnet/minecraft/ChatFormatting;"
            ),
            cancellable = true
    )
    private static void onFormattingCode(
        String string, int offset, Style currentStyle, Style resetStyle, FormattedCharSink output,
        CallbackInfoReturnable<Boolean> cir, @Local(name = "style") Style style, @Local(name = "i") LocalIntRef index, @Local(name = "code") char formattingCode
    ) {
        if (formattingCode == '^') {
            var matcher = RubyText.RUBY_PATTERN.matcher(string).region(index.get(), string.length());
            if (matcher.lookingAt()) {
                var rubyText = RubyText.fromFormatted(matcher.group(1), matcher.group(2), style);
                var rubyStyle = ((IRubyStyle) (Object) style).rubi$withRuby(rubyText);
                if (!output.accept(index.get(), rubyStyle, '￼')) {
                    cir.setReturnValue(false);
                    return;
                }

                index.set(matcher.end() - 2);
            }
        }
    }
}
