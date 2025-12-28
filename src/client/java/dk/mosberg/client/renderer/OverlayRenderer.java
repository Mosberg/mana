// OverlayRenderer.java - FIXED
package dk.mosberg.client.renderer;

import dk.mosberg.client.util.ColorHelper;
import dk.mosberg.client.util.RenderHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * HUD overlay rendering utilities for health bars, progress bars, and indicators. Provides reusable
 * components for custom HUD elements.
 */
public class OverlayRenderer {

        // Default styling constants
        private static final int DEFAULT_BACKGROUND_COLOR = ColorHelper.argb(128, 0, 0, 0);
        private static final int DEFAULT_BORDER_COLOR = ColorHelper.rgb(255, 255, 255);
        private static final int DEFAULT_LOW_HEALTH_COLOR = ColorHelper.rgb(255, 0, 0);
        private static final int DEFAULT_HIGH_HEALTH_COLOR = ColorHelper.rgb(0, 255, 0);

        /**
         * Initializes overlay systems.
         */
        public static void initialize() {
                // Initialize overlay systems if needed
        }

        /**
         * Draws a health bar with gradient coloring based on health percentage.
         *
         * @param context The draw context
         * @param x The x position
         * @param y The y position
         * @param width The bar width
         * @param height The bar height
         * @param current The current health
         * @param max The maximum health
         */
        public static void drawHealthBar(DrawContext context, int x, int y, int width, int height,
                        float current, float max) {
                if (max <= 0) {
                        return;
                }

                float percent = Math.max(0.0f, Math.min(current / max, 1.0f));

                // Draw background
                RenderHelper.drawRectangle(context, x, y, x + width, y + height,
                                DEFAULT_BACKGROUND_COLOR);

                // Draw health fill with gradient color
                int fillWidth = (int) (width * percent);
                int healthColor = ColorHelper.lerp(DEFAULT_LOW_HEALTH_COLOR,
                                DEFAULT_HIGH_HEALTH_COLOR, percent);
                RenderHelper.drawRectangle(context, x, y, x + fillWidth, y + height, healthColor);

                // Draw border
                drawBorder(context, x, y, width, height, DEFAULT_BORDER_COLOR);
        }

        /**
         * Draws a progress bar with custom color.
         *
         * @param context The draw context
         * @param x The x position
         * @param y The y position
         * @param width The bar width
         * @param height The bar height
         * @param progress The progress (0.0-1.0)
         * @param color The fill color
         */
        public static void drawProgressBar(DrawContext context, int x, int y, int width, int height,
                        float progress, int color) {
                progress = Math.max(0.0f, Math.min(progress, 1.0f));

                // Draw background
                RenderHelper.drawRectangle(context, x, y, x + width, y + height,
                                DEFAULT_BACKGROUND_COLOR);

                // Draw progress fill
                int fillWidth = (int) (progress * width);
                if (fillWidth > 0) {
                        RenderHelper.drawRectangle(context, x, y, x + fillWidth, y + height, color);
                }

                // Draw border
                drawBorder(context, x, y, width, height, DEFAULT_BORDER_COLOR);
        }

        /**
         * Draws a resource bar (mana, stamina, etc.).
         *
         * @param context The draw context
         * @param x The x position
         * @param y The y position
         * @param width The bar width
         * @param height The bar height
         * @param current The current amount
         * @param max The maximum amount
         * @param color The bar color
         */
        public static void drawResourceBar(DrawContext context, int x, int y, int width, int height,
                        float current, float max, int color) {
                if (max <= 0) {
                        return;
                }

                float progress = current / max;
                drawProgressBar(context, x, y, width, height, progress, color);
        }

        /**
         * Draws a cooldown indicator (circular with pie-slice fill).
         *
         * @param context The draw context
         * @param centerX The center x position
         * @param centerY The center y position
         * @param radius The radius
         * @param progress The cooldown progress (0.0-1.0, where 1.0 is ready)
         * @param color The indicator color
         */
        public static void drawCooldownIndicator(DrawContext context, int centerX, int centerY,
                        float radius, float progress, int color) {
                progress = Math.max(0.0f, Math.min(progress, 1.0f));

                if (progress < 1.0f) {
                        int segments = 32;
                        int endSegment = (int) (segments * progress);

                        // Draw filled segments
                        for (int i = 0; i < endSegment; i++) {
                                double angle1 = 2 * Math.PI * i / segments - Math.PI / 2;
                                double angle2 = 2 * Math.PI * (i + 1) / segments - Math.PI / 2;

                                int x1 = centerX + (int) (Math.cos(angle1) * radius);
                                int y1 = centerY + (int) (Math.sin(angle1) * radius);
                                int x2 = centerX + (int) (Math.cos(angle2) * radius);
                                int y2 = centerY + (int) (Math.sin(angle2) * radius);

                                // Draw line from center to edge
                                RenderHelper.drawLine(context, centerX, centerY, x1, y1, color);
                                RenderHelper.drawLine(context, centerX, centerY, x2, y2, color);

                                // Draw outer edge segment
                                RenderHelper.drawLine(context, x1, y1, x2, y2, color);
                        }
                }

                // Draw circle outline
                RenderHelper.drawCircle(context, centerX, centerY, radius, DEFAULT_BORDER_COLOR,
                                32);
        }

        /**
         * Draws a vertical bar (e.g., for experience or charge indicators).
         *
         * @param context The draw context
         * @param x The x position
         * @param y The y position (top)
         * @param width The bar width
         * @param height The bar height
         * @param progress The fill progress (0.0-1.0, fills from bottom to top)
         * @param color The fill color
         */
        public static void drawVerticalBar(DrawContext context, int x, int y, int width, int height,
                        float progress, int color) {
                progress = Math.max(0.0f, Math.min(progress, 1.0f));

                // Draw background
                RenderHelper.drawRectangle(context, x, y, x + width, y + height,
                                DEFAULT_BACKGROUND_COLOR);

                // Draw fill (from bottom to top)
                int fillHeight = (int) (height * progress);
                if (fillHeight > 0) {
                        RenderHelper.drawRectangle(context, x, y + height - fillHeight, x + width,
                                        y + height, color);
                }

                // Draw border
                drawBorder(context, x, y, width, height, DEFAULT_BORDER_COLOR);
        }

        /**
         * Helper method to draw a border around a rectangle.
         *
         * @param context The draw context
         * @param x The x position
         * @param y The y position
         * @param width The width
         * @param height The height
         * @param color The border color
         */
        private static void drawBorder(DrawContext context, int x, int y, int width, int height,
                        int color) {
                RenderHelper.drawRectangle(context, x, y, x + width, y + 1, color); // Top
                RenderHelper.drawRectangle(context, x, y + height - 1, x + width, y + height,
                                color); // Bottom
                RenderHelper.drawRectangle(context, x, y, x + 1, y + height, color); // Left
                RenderHelper.drawRectangle(context, x + width - 1, y, x + width, y + height, color); // Right
        }

        /**
         * Draws a percentage text overlay on a bar.
         *
         * @param context The draw context
         * @param x The x position (center of text)
         * @param y The y position (center of text)
         * @param current The current value
         * @param max The maximum value
         * @param color The text color
         */
        public static void drawPercentageText(DrawContext context, int x, int y, float current,
                        float max, int color) {
                if (max <= 0) {
                        return;
                }

                int percent = (int) ((current / max) * 100);
                String text = percent + "%";

                // Get MinecraftClient and TextRenderer
                MinecraftClient client = MinecraftClient.getInstance();

                // Center the text
                int textWidth = client.textRenderer.getWidth(text);
                context.drawText(client.textRenderer, text, x - textWidth / 2, y, color, true);
        }
}
