package fun.lofe.amber.ruby;

import fun.lofe.amber.RubyText;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public interface RubyStyle {

    static @Nullable RubyText getRubyText(Style style) {
        return asRubyStyle(style).rubi$getRuby();
    }

    static RubyStyle asRubyStyle(Style style) {
        return (RubyStyle) (Object) style;
    }

    Style rubi$withRuby(RubyText rubyText);

    @Nullable RubyText rubi$getRuby();
}
