package com.kevinsundqvistnorlen.rubi.mixin.client;

import com.kevinsundqvistnorlen.rubi.IRubyStyle;
import com.kevinsundqvistnorlen.rubi.RubyText;
import net.minecraft.network.chat.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Style.class)
public abstract class MixinStyle implements IRubyStyle {

    @Final @Shadow private TextColor color;
    @Final @Shadow private Boolean bold;
    @Final @Shadow private Boolean italic;
    @Final @Shadow private Boolean underlined;
    @Final @Shadow private Boolean strikethrough;
    @Final @Shadow private Boolean obfuscated;
    @Final @Shadow private ClickEvent clickEvent;
    @Final @Shadow private HoverEvent hoverEvent;
    @Final @Shadow private String insertion;
    @Final @Shadow private FontDescription font;

    @Unique private @Nullable RubyText ruby;

    @Invoker("<init>")
    private static @NotNull Style invokeConstructor(
        @Nullable TextColor color, final @Nullable  Integer shadowColor, @Nullable Boolean bold,
        @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough,
        @Nullable Boolean obfuscated, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent,
        @Nullable String insertion, @Nullable FontDescription font
    ) {
        return Style.EMPTY;
    }

    @Shadow
    public abstract boolean equals(Object o);

    @Shadow @Final private @org.jspecify.annotations.Nullable Integer shadowColor;

    @Override
    public Style rubi$withRuby(RubyText rubyText) {
        var result = MixinStyle.invokeConstructor(
            this.color, this.shadowColor, this.bold,
            this.italic, this.underlined, this.strikethrough,
            this.obfuscated, this.clickEvent, this.hoverEvent,
            this.insertion, this.font
        );
        //noinspection DataFlowIssue
        ((MixinStyle) (Object) result).setRuby(rubyText);
        return result;
    }

    @Override
    public @Nullable RubyText rubi$getRuby() {
        return this.ruby;
    }

    @Unique
    private void setRuby(@Nullable RubyText ruby) {
        this.ruby = ruby;
    }

    @Inject(method = "applyTo", at = @At("RETURN"))
    private void onWithParent(Style other, CallbackInfoReturnable<Style> cir) {
        if (cir.getReturnValue() == other) return;
        if (IRubyStyle.getRuby(other).isEmpty()) return;
        ((MixinStyle) (Object) cir.getReturnValue()).setRuby(((IRubyStyle) (Object) other).rubi$getRuby());
    }

    @Inject(method = "equals", at = @At("RETURN"), cancellable = true)
    private void onEquals(Object o, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            cir.setReturnValue(Objects.equals(this.ruby, ((IRubyStyle) o).rubi$getRuby()));
        }
    }

    @Inject(method = "hashCode", at = @At("RETURN"), cancellable = true)
    private void onHashCode(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Objects.hash(cir.getReturnValue(), this.ruby));
    }
}
