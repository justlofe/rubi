package fun.lofe.amber;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.UnaryOperator;

public final class Utils {

    static FormattedCharSequence transformStyle(FormattedCharSequence text, UnaryOperator<Style> transformer) {
        return visitor -> text.accept(
            (index, style, codePoint) -> visitor.accept(index, transformer.apply(style), codePoint)
        );
    }

}
