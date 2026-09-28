package fun.lofe.amber.mixin.client;

import fun.lofe.amber.option.RubyRenderMode;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "processDumpedOptions", at = @At("HEAD"))
    private void onAccept(Options.OptionAccess access, CallbackInfo info) {
        RubyRenderMode.accept(access);
    }
}
