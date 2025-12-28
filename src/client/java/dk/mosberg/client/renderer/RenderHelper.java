package dk.mosberg.client.renderer;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Client-side rendering utilities for textures, shapes, and effects.
 */
public class RenderHelper {

    public static void initialize() {
        // Initialize any rendering resources
    }

    /**
     * Draws a textured rectangle.
     *
     * @param context The draw context
     * @param texture The texture identifier
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     */
    public static void drawTexture(DrawContext context, Identifier texture, int x, int y, int width,
            int height) {
        // renderLayer, texture, x, y, u, v, width, height, textureWidth, textureHeight
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, width, height,
                width, height);
    }

    /**
     * Draws a filled rectangle.
     *
     * @param context The draw context
     * @param x1 The starting x position
     * @param y1 The starting y position
     * @param x2 The ending x position
     * @param y2 The ending y position
     * @param color The color (ARGB)
     */
    public static void drawRectangle(DrawContext context, int x1, int y1, int x2, int y2,
            int color) {
        context.fill(x1, y1, x2, y2, color);
    }

    /**
     * Draws a gradient rectangle.
     *
     * @param context The draw context
     * @param x1 The starting x position
     * @param y1 The starting y position
     * @param x2 The ending x position
     * @param y2 The ending y position
     * @param colorStart The starting color (ARGB)
     * @param colorEnd The ending color (ARGB)
     */
    public static void drawGradient(DrawContext context, int x1, int y1, int x2, int y2,
            int colorStart, int colorEnd) {
        context.fillGradient(x1, y1, x2, y2, colorStart, colorEnd);
    }

    /**
     * Draws a circle outline. NOTE: This is a simplified version that uses DrawContext methods. For
     * custom rendering, consider using a custom widget or HUD element.
     *
     * @param context The draw context
     * @param centerX The center x position
     * @param centerY The center y position
     * @param radius The radius
     * @param color The color (ARGB)
     * @param segments The number of segments (higher = smoother)
     */
    public static void drawCircle(DrawContext context, int centerX, int centerY, float radius,
            int color, int segments) {
        // Approximate circle with line segments
        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            int x1 = centerX + (int) (Math.cos(angle1) * radius);
            int y1 = centerY + (int) (Math.sin(angle1) * radius);
            int x2 = centerX + (int) (Math.cos(angle2) * radius);
            int y2 = centerY + (int) (Math.sin(angle2) * radius);

            // Draw line segment
            drawLineSegment(context, x1, y1, x2, y2, color);
        }
    }

    /**
     * Draws a line between two points. NOTE: This is a simplified version using fill() for small
     * rectangles. For proper line rendering, use a custom HUD element with BufferBuilder.
     *
     * @param context The draw context
     * @param x1 The starting x position
     * @param y1 The starting y position
     * @param x2 The ending x position
     * @param y2 The ending y position
     * @param color The color (ARGB)
     */
    public static void drawLine(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        drawLineSegment(context, x1, y1, x2, y2, color);
    }

    /**
     * Helper method to draw a line segment using rectangles. This is a workaround since direct
     * BufferBuilder access should be done through HUD elements in 1.21.10+.
     *
     * @param context The draw context
     * @param x1 Starting x
     * @param y1 Starting y
     * @param x2 Ending x
     * @param y2 Ending y
     * @param color Line color
     */
    private static void drawLineSegment(DrawContext context, int x1, int y1, int x2, int y2,
            int color) {
        // Calculate distance and angle
        double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        int points = Math.max(1, (int) distance);

        // Draw points along the line
        for (int i = 0; i <= points; i++) {
            double t = points > 0 ? (double) i / points : 0;
            int x = (int) (x1 + (x2 - x1) * t);
            int y = (int) (y1 + (y2 - y1) * t);
            context.fill(x, y, x + 1, y + 1, color);
        }
    }

    /**
     * Draws a bordered rectangle.
     *
     * @param context The draw context
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param fillColor The fill color (ARGB)
     * @param borderColor The border color (ARGB)
     */
    public static void drawBorderedRectangle(DrawContext context, int x, int y, int width,
            int height, int fillColor, int borderColor) {
        // Draw fill
        context.fill(x, y, x + width, y + height, fillColor);
        // Draw border
        context.drawStrokedRectangle(x, y, width, height, borderColor);
    }
}
