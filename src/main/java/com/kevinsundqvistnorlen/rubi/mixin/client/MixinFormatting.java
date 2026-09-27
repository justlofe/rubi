package com.kevinsundqvistnorlen.rubi.mixin.client;

import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.regex.Pattern;

@Mixin(ChatFormatting.class)
public abstract class MixinFormatting {

    @SuppressWarnings("unused")
    @Shadow
    private static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR^]");
}
