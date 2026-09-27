package com.kevinsundqvistnorlen.rubi;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;

import java.util.ArrayList;
import java.util.List;

public final class CompositePreparedText implements Font.PreparedText {

    private final List<Font.PreparedText> parts = new ArrayList<>();

    public void add(Font.PreparedText text) {
        parts.add(text);
    }

    @Override
    public void visit(Font.GlyphVisitor visitor) {
        for (Font.PreparedText part : parts) {
            part.visit(visitor);
        }
    }

    @Override
    public ScreenRectangle bounds() {
        ScreenRectangle result = null;

        for (Font.PreparedText part : parts) {
            ScreenRectangle bounds = part.bounds();

            if (bounds == null) {
                continue;
            }

            result = result == null ? bounds : union(result, bounds);
        }

        return result;
    }

    private static ScreenRectangle union(ScreenRectangle a, ScreenRectangle b) {
        int left = Math.min(a.left(), b.left());
        int top = Math.min(a.top(), b.top());

        int right = Math.max(
                a.left() + a.width(),
                b.left() + b.width()
        );

        int bottom = Math.max(
                a.top() + a.height(),
                b.top() + b.height()
        );

        return new ScreenRectangle(
                left,
                top,
                right - left,
                bottom - top
        );
    }
}