// TextHelper.java - FIXED
package dk.mosberg.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class TextHelper {

    public static void initialize() {
        // Initialize text helper systems if needed
    }

    /**
     * Formats a string by replacing placeholders with provided values.
     *
     * @param template The string template with placeholders (e.g., "Hello, {name}!")
     * @param values The values to replace the placeholders (e.g., {"name": "World"})
     * @return The formatted string (e.g., "Hello, World!")
     */
    public static String formatString(String template, java.util.Map<String, String> values) {
        String formatted = template;
        for (java.util.Map.Entry<String, String> entry : values.entrySet()) {
            formatted = formatted.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return formatted;
    }

    /**
     * Truncates a string to a specified length, adding ellipsis if necessary.
     *
     * @param text The original string
     * @param maxLength The maximum allowed length
     * @return The truncated string with ellipsis if truncated
     */
    public static String truncateString(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }

        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }

        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Converts a string to title case.
     *
     * @param text The original string
     * @return The string converted to title case
     */
    public static String toTitleCase(String text) {
        String[] words = text.split(" ");
        StringBuilder titleCase = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase()).append(" ");
            }
        }

        return titleCase.toString().trim();
    }

    /**
     * Text Renderer Utilities
     *
     * @param context The draw context
     * @param text The text to render
     * @param x The x position
     * @param y The y position
     * @param color The text color
     * @return The width of the rendered text
     */
    public static int renderText(DrawContext context, String text, int x, int y, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        context.drawText(client.textRenderer, text, x, y, color, true);
        return client.textRenderer.getWidth(text);
    }
}
