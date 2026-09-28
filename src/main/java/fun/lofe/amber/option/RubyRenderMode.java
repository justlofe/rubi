package fun.lofe.amber.option;

import fun.lofe.amber.Amber;
import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public enum RubyRenderMode {

    HIDDEN("hidden"),
    ABOVE("above"),
    BELOW("below"),
    REPLACE("replace");

    private final String translationKey;

    RubyRenderMode(String name) {
        this.translationKey = Option.TRANSLATION_KEY + "." + name;
    }

    public static void accept(Options.OptionAccess access) {
        access.process("amber.renderMode", Option.INSTANCE);
    }

    public static OptionInstance<RubyRenderMode> getOption() {
        return Option.INSTANCE;
    }

    private static RubyRenderMode byId(int id) {
        return RubyRenderMode.values()[id];
    }

    private int getId() {
        return this.ordinal();
    }

    private static final class Option {

        static final String TRANSLATION_KEY = "options.amber.renderMode";

        static final OptionInstance<RubyRenderMode> INSTANCE = new OptionInstance<>(
            TRANSLATION_KEY,
            OptionInstance.noTooltip(),
            (_, value) -> Component.translatable(value.translationKey),
            new OptionInstance.Enum<>(
                Arrays.asList(RubyRenderMode.values()),
                Codec.INT.xmap(RubyRenderMode::byId, RubyRenderMode::getId)
            ),
            RubyRenderMode.ABOVE,
            (value) -> Amber.LOGGER.debug("Ruby display mode changed to {} ({})", value, value.ordinal())
        );

    }
}
