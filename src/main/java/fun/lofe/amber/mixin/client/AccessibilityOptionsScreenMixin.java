package fun.lofe.amber.mixin.client;

import fun.lofe.amber.option.RubyRenderMode;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {

    /**
     * The goal of this injection is to add an option from our mod
     */
    @Inject(method = "options", at = @At("RETURN"), cancellable = true)
    private static void onGetOptions(CallbackInfoReturnable<OptionInstance<?>[]> info) {
        OptionInstance<?>[] options = info.getReturnValue();
        OptionInstance<?>[] newOptions = Arrays.copyOf(options, options.length + 1);
        newOptions[options.length] = RubyRenderMode.getOption();
        info.setReturnValue(newOptions);
    }

}
