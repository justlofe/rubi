package com.kevinsundqvistnorlen.rubi;

import net.minecraft.client.StringSplitter;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

@FunctionalInterface
public interface TextDrawer {

    static float draw(
            FormattedCharSequence text,
            float x,
            float y,
            Matrix3x2fc pose,
            StringSplitter splitter,
            int fontHeight,
            TextDrawer textDrawer
    ) {
        var xx = new MutableFloat(x);

        text.accept((index, style, codePoint) -> {
            xx.add(
                    IRubyStyle.getRuby(style)
                            .map(rubyText ->
                                    rubyText.draw(
                                            xx.floatValue(),
                                            y,
                                            pose,
                                            splitter,
                                            fontHeight,
                                            textDrawer
                                    )
                            )
                            .orElseGet(() -> {
                                var styledChar = FormattedCharSequence.codepoint(codePoint, style);

                                textDrawer.draw(
                                        styledChar,
                                        xx.floatValue(),
                                        y,
                                        pose
                                );

                                return splitter.stringWidth(styledChar);
                            })
            );

            return true;
        });

        return xx.getValue();
    }

    void draw(
            FormattedCharSequence text,
            float x,
            float y,
            Matrix3x2fc pose
    );

    default void drawScaled(
            FormattedCharSequence text,
            float x,
            float y,
            float scale,
            Matrix3x2fc pose
    ) {
        this.draw(
                text,
                x,
                y,
                new Matrix3x2f(pose).scaleAround(scale, x, y)
        );
    }

    default void drawSpacedApart(
            FormattedCharSequence text,
            float x,
            float y,
            float scale,
            float boxWidth,
            Matrix3x2fc pose,
            StringSplitter handler
    ) {
        float textWidth = handler.stringWidth(text) * scale;
        float emptySpace = boxWidth - textWidth;

        // Avoid division by zero for empty ruby/base text.
        if (textWidth <= 0.0f) {
            return;
        }

        var xx = new MutableFloat(x);

        text.accept((index, style, codePoint) -> {
            var styledChar = FormattedCharSequence.codepoint(codePoint, style);

            float charWidth = handler.stringWidth(styledChar) * scale;
            float spaceAround = emptySpace * (charWidth / textWidth);

            xx.add(spaceAround / 2.0f);

            this.drawScaled(
                    styledChar,
                    xx.floatValue(),
                    y,
                    scale,
                    pose
            );

            xx.add(charWidth + spaceAround / 2.0f);

            return true;
        });
    }
}