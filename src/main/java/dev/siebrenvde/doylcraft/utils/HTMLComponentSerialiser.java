package dev.siebrenvde.doylcraft.utils;

import j2html.TagCreator;
import j2html.tags.specialized.SpanTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.ComponentEncoder;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static io.papermc.paper.adventure.PaperAdventure.FLATTENER;

@NullMarked
public final class HTMLComponentSerialiser implements ComponentEncoder<Component, String> {

    private static final HTMLComponentSerialiser INSTANCE = new HTMLComponentSerialiser();

    public static HTMLComponentSerialiser html() {
        return INSTANCE;
    }

    @Override
    public String serialize(Component component) {
        SpanTag span = toSpan(component);
        return span.render();
    }

    private SpanTag toSpan(Component component) {
        SpanTag span = TagCreator.span();
        FLATTENER.flatten(component, getListener(span));
        if (component.hasStyling()) {
            List<String> css = new ArrayList<>();

            TextColor colour = component.color();
            if (colour != null) {
                css.add("color: " + colour.asHexString() + ";");
            }

            ShadowColor shadow = component.shadowColor();
            if (shadow != null) {
                css.add("text-shadow: 3px 3px " + shadow.asHexString() + ";");
            }

            TextDecoration.State bold = component.decoration(TextDecoration.BOLD);
            if (bold == TextDecoration.State.TRUE) {
                css.add("font-weight: bold;");
            } else if (bold == TextDecoration.State.FALSE) {
                css.add("font-weight: normal;");
            }

            TextDecoration.State italic = component.decoration(TextDecoration.ITALIC);
            if (italic == TextDecoration.State.TRUE) {
                css.add("font-style: italic;");
            } else if (italic == TextDecoration.State.FALSE) {
                css.add("font-style: normal;");
            }

            List<String> decorations = new ArrayList<>();

            if (component.decoration(TextDecoration.STRIKETHROUGH) == TextDecoration.State.TRUE) {
                decorations.add("line-through");
            }

            if (component.decoration(TextDecoration.UNDERLINED) == TextDecoration.State.TRUE) {
                decorations.add("underline");
            }

            if (!decorations.isEmpty()) {
                css.add("text-decoration: " + String.join(" ", decorations) + ";");
            }

            span.withStyle(String.join(" ", css));
        }
        component.children().stream().map(this::toSpan).forEach(span::with);
        return span;
    }

    private FlattenerListener getListener(SpanTag span) {
        return new FlattenerListener() {
            @Override
            public void component(String text) {
                span.withText(text);
            }

            @Override
            public boolean shouldContinue() {
                return false;
            }
        };
    }

}
