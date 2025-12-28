// RenderHelper.java - Already correct, no changes needed
package dk.mosberg.client.util;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Client-side rendering utilities for textures, shapes, and effects. Optimized for minimal draw
 * calls and consistent parameter naming.
 */
public class RenderHelper {

    /**
     * Initializes rendering resources.
     */
    public static void initialize() {
        // Initialize any rendering resources if needed
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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0f, 0.0f, width, height,
                width, height);
    }

    /**
     * Draws a filled rectangle.
     *
     * @param context The draw context
     * @param startX The starting x position
     * @param startY The starting y position
     * @param endX The ending x position
     * @param endY The ending y position
     * @param color The color (ARGB)
     */
    public static void drawRectangle(DrawContext context, int startX, int startY, int endX,
            int endY, int color) {
        context.fill(startX, startY, endX, endY, color);
    }

    /**
     * Draws a gradient rectangle.
     *
     * @param context The draw context
     * @param startX The starting x position
     * @param startY The starting y position
     * @param endX The ending x position
     * @param endY The ending y position
     * @param colorStart The starting color (ARGB)
     * @param colorEnd The ending color (ARGB)
     */
    public static void drawGradient(DrawContext context, int startX, int startY, int endX, int endY,
            int colorStart, int colorEnd) {
        context.fillGradient(startX, startY, endX, endY, colorStart, colorEnd);
    }

    /**
     * Draws a circle outline using line segments. Note: For better performance with many circles,
     * consider using custom HUD elements.
     *
     * @param context The draw context
     * @param centerX The center x position
     * @param centerY The center y position
     * @param radius The radius
     * @param color The color (ARGB)
     * @param segments The number of segments (higher = smoother, default: 32)
     */
    public static void drawCircle(DrawContext context, int centerX, int centerY, float radius,
            int color, int segments) {
        if (segments < 3) {
            segments = 32; // Minimum sensible segment count
        }

        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            int x1 = centerX + (int) (Math.cos(angle1) * radius);
            int y1 = centerY + (int) (Math.sin(angle1) * radius);
            int x2 = centerX + (int) (Math.cos(angle2) * radius);
            int y2 = centerY + (int) (Math.sin(angle2) * radius);

            drawLine(context, x1, y1, x2, y2, color);
        }
    }

    /**
     * Draws a line between two points using Bresenham's line algorithm. Optimized to reduce fill()
     * calls.
     *
     * @param context The draw context
     * @param startX The starting x position
     * @param startY The starting y position
     * @param endX The ending x position
     * @param endY The ending y position
     * @param color The color (ARGB)
     */
    public static void drawLine(DrawContext context, int startX, int startY, int endX, int endY,
            int color) {
        int dx = Math.abs(endX - startX);
        int dy = Math.abs(endY - startY);
        int sx = startX < endX ? 1 : -1;
        int sy = startY < endY ? 1 : -1;
        int err = dx - dy;

        int x = startX;
        int y = startY;

        while (true) {
            context.fill(x, y, x + 1, y + 1, color);

            if (x == endX && y == endY) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }

            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    /**
     * Draws a bordered rectangle with fill and outline.
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

        // Draw border using stroked rectangle
        context.drawStrokedRectangle(x, y, width, height, borderColor);
    }

    /**
     * Draws a rounded rectangle (approximation using small rectangles).
     *
     * @param context The draw context
     * @param x The x position
     * @param y The y position
     * @param width The width
     * @param height The height
     * @param radius The corner radius
     * @param color The color (ARGB)
     */
    public static void drawRoundedRectangle(DrawContext context, int x, int y, int width,
            int height, int radius, int color) {
        // Draw main rectangle (without corners)
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);

        // Draw rounded corners (simplified)
        drawFilledCircleQuarter(context, x + radius, y + radius, radius, color, 2); // Top-left
        drawFilledCircleQuarter(context, x + width - radius - 1, y + radius, radius, color, 1); // Top-right
        drawFilledCircleQuarter(context, x + radius, y + height - radius - 1, radius, color, 3); // Bottom-left
        drawFilledCircleQuarter(context, x + width - radius - 1, y + height - radius - 1, radius,
                color, 4); // Bottom-right
    }

    /**
     * Helper method to draw a filled quarter circle.
     *
     * @param context The draw context
     * @param centerX Center x position
     * @param centerY Center y position
     * @param radius The radius
     * @param color The color
     * @param quarter Which quarter (1=top-right, 2=top-left, 3=bottom-left, 4=bottom-right)
     */
    private static void drawFilledCircleQuarter(DrawContext context, int centerX, int centerY,
            int radius, int color, int quarter) {
        for (int dx = 0; dx <= radius; dx++) {
            for (int dy = 0; dy <= radius; dy++) {
                if (dx * dx + dy * dy <= radius * radius) {
                    int px = centerX + (quarter == 1 || quarter == 4 ? dx : -dx);
                    int py = centerY + (quarter == 3 || quarter == 4 ? dy : -dy);
                    context.fill(px, py, px + 1, py + 1, color);
                }
            }
        }
    }
}
