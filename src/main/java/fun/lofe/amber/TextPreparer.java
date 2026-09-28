package fun.lofe.amber;

import fun.lofe.amber.ruby.RubyStyle;
import net.minecraft.client.StringSplitter;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

@FunctionalInterface
public interface TextPreparer {

    void prepare(FormattedCharSequence text, float x, float y, Matrix3x2fc pose);

    static void prepare(FormattedCharSequence text, float x, float y, Matrix3x2fc pose, StringSplitter splitter, int fontHeight, TextPreparer textPreparer) {
        MutableFloat xValue = new MutableFloat(x);
        text.accept((_, style, codePoint) -> {
            RubyText rubyText = RubyStyle.getRubyText(style);
            if(rubyText != null) {
                xValue.add(rubyText.draw(xValue.floatValue(), y, pose, splitter, fontHeight, textPreparer));
                return true;
            }

            FormattedCharSequence styledChar = FormattedCharSequence.codepoint(codePoint, style);
            textPreparer.prepare(styledChar, xValue.floatValue(), y, pose);

            xValue.add(splitter.stringWidth(styledChar));

            return true;
        });
    }

    default void prepareScaled(FormattedCharSequence text, float x, float y, float scale, Matrix3x2fc pose) {
        this.prepare(text, x, y, new Matrix3x2f(pose).scaleAround(scale, x, y));
    }

    default void prepareSpacedApart(FormattedCharSequence text, float x, float y, float scale, float boxWidth, Matrix3x2fc pose, StringSplitter splitter) {
        float textWidth = splitter.stringWidth(text) * scale;
        float emptySpace = boxWidth - textWidth;

        // Avoid division by zero for empty ruby/base text.
        if (textWidth <= 0.0f) return;

        var xValue = new MutableFloat(x);

        text.accept((index, style, codePoint) -> {
            var styledChar = FormattedCharSequence.codepoint(codePoint, style);

            float charWidth = splitter.stringWidth(styledChar) * scale;
            float spaceAround = emptySpace * (charWidth / textWidth);

            xValue.add(spaceAround / 2.0f);

            this.prepareScaled(styledChar, xValue.floatValue(), y, scale, pose);
            xValue.add(charWidth + spaceAround / 2.0f);

            return true;
        });
    }
}