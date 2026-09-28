package fun.lofe.amber.mixin.client;

import fun.lofe.amber.RubyText;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FormattedText.class)
public interface FormattedTextMixin {

    @Inject(method = "getString", at = @At("RETURN"), cancellable = true)
    default void onGetString(CallbackInfoReturnable<String> info) {
        info.setReturnValue(RubyText.strip(info.getReturnValue()));
    }

}
