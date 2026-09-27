package com.kevinsundqvistnorlen.rubi;

import com.kevinsundqvistnorlen.rubi.option.RubyRenderMode;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fc;

import java.util.Objects;
import java.util.regex.Pattern;

public record RubyText(
        FormattedCharSequence text,
        FormattedCharSequence ruby
) {

    public static final Pattern RUBY_PATTERN =
            Pattern.compile("§\\^\\s*(.+?)\\s*\\(\\s*(.+?)\\s*\\)");

    private static final float RUBY_SCALE = 0.5f;
    private static final float RUBY_OVERLAP = 0.1f;
    private static final float TEXT_SCALE = 0.8f;

    public static String strip(String returnValue) {
        StringBuilder sb = new StringBuilder(returnValue.length());

        var matcher = RUBY_PATTERN.matcher(returnValue);

        while (matcher.find()) {
            if (RubyRenderMode.getOption().get() == RubyRenderMode.REPLACE) {
                matcher.appendReplacement(sb, matcher.group(2));
            } else {
                matcher.appendReplacement(sb, matcher.group(1));
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static @NotNull RubyText fromFormatted(
            String word,
            String ruby,
            Style style
    ) {
        var formattedWord = FormattedCharSequence.composite(
                v -> StringDecomposer.iterateFormatted(
                        word,
                        0,
                        style,
                        style,
                        v
                )
        );

        var formattedRuby = FormattedCharSequence.composite(
                v -> StringDecomposer.iterateFormatted(
                        ruby,
                        0,
                        style,
                        style,
                        v
                )
        );

        formattedRuby = Utils.transformStyle(
                formattedRuby,
                s -> s.withUnderlined(false)
                        .withStrikethrough(false)
        );

        return new RubyText(formattedWord, formattedRuby);
    }

    float draw(
            float x,
            float y,
            Matrix3x2fc pose,
            StringSplitter splitter,
            int fontHeight,
            TextDrawer textDrawer
    ) {
        float width = this.getWidth(splitter);

        switch (RubyRenderMode.getOption().get()) {
            case ABOVE ->
                    this.drawAbove(
                            x, y, width, pose, splitter, fontHeight, textDrawer
                    );

            case BELOW ->
                    this.drawBelow(
                            x, y, width, pose, splitter, fontHeight, textDrawer
                    );

            case REPLACE ->
                    this.drawReplace(x, y, pose, textDrawer);

            case HIDDEN ->
                    this.drawHidden(x, y, pose, textDrawer);
        }

        return width;
    }

    public float getWidth(StringSplitter textHandler) {
        var mode = RubyRenderMode.getOption().get();

        float baseWidth = 0.0f;
        float rubyWidth = 0.0f;

        if (mode != RubyRenderMode.REPLACE) {
            baseWidth += textHandler.stringWidth(this.text());
        }

        if (mode != RubyRenderMode.HIDDEN) {
            rubyWidth += textHandler.stringWidth(this.ruby());
        }

        return switch (mode) {
            case ABOVE, BELOW ->
                    Math.max(
                            baseWidth * TEXT_SCALE,
                            rubyWidth * RUBY_SCALE
                    );

            case HIDDEN ->
                    baseWidth;

            case REPLACE ->
                    rubyWidth;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        if (this == o) {
            return true;
        }

        RubyText other = (RubyText) o;

        return Objects.equals(this.text(), other.text())
                && Objects.equals(this.ruby(), other.ruby());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.text(), this.ruby());
    }

    private void drawRubyPair(
            float x,
            float yText,
            float yRuby,
            float width,
            TextDrawer textDrawer,
            StringSplitter textHandler,
            Matrix3x2fc pose
    ) {
        textDrawer.drawSpacedApart(
                this.text(),
                x,
                yText,
                TEXT_SCALE,
                width,
                pose,
                textHandler
        );

        textDrawer.drawSpacedApart(
                this.ruby(),
                x,
                yRuby,
                RUBY_SCALE,
                width,
                pose,
                textHandler
        );
    }

    private void drawAbove(
            float x,
            float y,
            float width,
            Matrix3x2fc pose,
            StringSplitter textHandler,
            int fontHeight,
            TextDrawer textDrawer
    ) {
        float textHeight = fontHeight * TEXT_SCALE;
        float rubyHeight = fontHeight * RUBY_SCALE;

        float yBody = y + (fontHeight - textHeight);
        float yAbove =
                yBody - rubyHeight + fontHeight * RUBY_OVERLAP;

        this.drawRubyPair(
                x,
                yBody,
                yAbove,
                width,
                textDrawer,
                textHandler,
                pose
        );
    }

    private void drawBelow(
            float x,
            float y,
            float width,
            Matrix3x2fc pose,
            StringSplitter textHandler,
            int fontHeight,
            TextDrawer textDrawer
    ) {
        float textHeight = fontHeight * TEXT_SCALE;

        float yBelow =
                y + textHeight - fontHeight * RUBY_OVERLAP;

        this.drawRubyPair(
                x,
                y,
                yBelow,
                width,
                textDrawer,
                textHandler,
                pose
        );
    }

    private void drawReplace(
            float x,
            float y,
            Matrix3x2fc pose,
            TextDrawer textDrawer
    ) {
        textDrawer.draw(
                this.ruby(),
                x,
                y,
                pose
        );
    }

    private void drawHidden(
            float x,
            float y,
            Matrix3x2fc pose,
            TextDrawer textDrawer
    ) {
        textDrawer.draw(
                this.text(),
                x,
                y,
                pose
        );
    }
}