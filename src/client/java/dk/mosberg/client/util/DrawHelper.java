// DrawHelper.java - FIXED
package dk.mosberg.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DrawHelper {

    public static void initialize() {
        // Initialize draw helper systems if needed
    }

    /**
     * Draws a border around a rectangle.
     *
     * @param context The draw context
     * @param x The x position
     * @param y The y position
     * @param width The rectangle width
     * @param height The rectangle height
     * @param color The border color
     */
    public static void drawBorder(DrawContext context, int x, int y, int width, int height,
            int color) {
        // Top border
        RenderHelper.drawRectangle(context, x, y, x + width, y + 1, color);
        // Bottom border
        RenderHelper.drawRectangle(context, x, y + height - 1, x + width, y + height, color);
        // Left border
        RenderHelper.drawRectangle(context, x, y, x + 1, y + height, color);
        // Right border
        RenderHelper.drawRectangle(context, x + width - 1, y, x + width, y + height, color);
    }

    /**
     * Clamps a value between a minimum and maximum.
     *
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Converts RGBA components to a single integer color.
     *
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @param a Alpha component (0-255)
     * @return The combined integer color
     */
    public static int rgbaToInt(int r, int g, int b, int a) {
        return ColorHelper.rgbaToInt(r, g, b, a);
    }

    /**
     * Converts RGB components to a single integer color.
     *
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @return The combined integer color
     */
    public static int rgbToInt(int r, int g, int b) {
        return ColorHelper.rgbToInt(r, g, b);
    }

    /**
     * Text Width Helper
     *
     * @param text The text to measure
     * @return The width of the text in pixels
     */
    public static int getTextWidth(String text) {
        return MinecraftClient.getInstance().textRenderer.getWidth(text);
    }

    /**
     * Text Height Helper
     *
     * @return The height of the text in pixels
     */
    public static int getTextHeight() {
        return MinecraftClient.getInstance().textRenderer.fontHeight;
    }
}
