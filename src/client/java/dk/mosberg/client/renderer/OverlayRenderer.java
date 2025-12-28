package dk.mosberg.client.renderer;

import net.minecraft.client.gui.DrawContext;


/**
 * HUD overlay rendering utilities for health bars, progress bars, and indicators.
 */
public class OverlayRenderer {

        public static void initialize() {
                // Initialize overlay systems
        }

        /**
         * Draws a health bar.
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
                // Background
                RenderHelper.drawRectangle(context, x, y, x + width, y + height,
                                ColorHelper.argb(128, 0, 0, 0));

                // Health fill
                int fillWidth = (int) ((current / max) * width);
                int healthColor = ColorHelper.lerp(ColorHelper.rgb(255, 0, 0), // Red at low health
                                ColorHelper.rgb(0, 255, 0), // Green at full health
                                current / max);
                RenderHelper.drawRectangle(context, x, y, x + fillWidth, y + height, healthColor);

                // Border
                RenderHelper.drawRectangle(context, x, y, x + width, y + 1,
                                ColorHelper.rgb(255, 255, 255));
                RenderHelper.drawRectangle(context, x, y + height - 1, x + width, y + height,
                                ColorHelper.rgb(255, 255, 255));
                RenderHelper.drawRectangle(context, x, y, x + 1, y + height,
                                ColorHelper.rgb(255, 255, 255));
                RenderHelper.drawRectangle(context, x + width - 1, y, x + width, y + height,
                                ColorHelper.rgb(255, 255, 255));
        }

        /**
         * Draws a progress bar.
         *
         * @param context The draw context
         * @param x The x position
         * @param y The y position
         * @param width The bar width
         * @param height The bar height
         * @param progress The progress (0-1)
         * @param color The fill color
         */
        public static void drawProgressBar(DrawContext context, int x, int y, int width, int height,
                        float progress, int color) {
                // Background
                RenderHelper.drawRectangle(context, x, y, x + width, y + height,
                                ColorHelper.argb(128, 0, 0, 0));

                // Progress fill
                int fillWidth = (int) (progress * width);
                RenderHelper.drawRectangle(context, x, y, x + fillWidth, y + height, color);

                // Border
                RenderHelper.drawRectangle(context, x, y, x + width, y + 1,
                                ColorHelper.rgb(255, 255, 255));
                RenderHelper.drawRectangle(context, x, y + height - 1, x + width, y + height,
                                ColorHelper.rgb(255, 255, 255));
                RenderHelper.drawRectangle(context, x, y, x + 1, y + height,
                                ColorHelper.rgb(255, 255, 255));
                RenderHelper.drawRectangle(context, x + width - 1, y, x + width, y + height,
                                ColorHelper.rgb(255, 255, 255));
        }

        /**
         * Draws a mana/stamina bar.
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
                drawProgressBar(context, x, y, width, height, current / max, color);
        }

        /**
         * Draws a cooldown indicator (circular).
         *
         * @param context The draw context
         * @param centerX The center x position
         * @param centerY The center y position
         * @param radius The radius
         * @param progress The cooldown progress (0-1, where 1 is ready)
         * @param color The indicator color
         */
        public static void drawCooldownIndicator(DrawContext context, int centerX, int centerY,
                        float radius, float progress, int color) {
                if (progress < 1.0f) {
                        // Draw cooldown overlay
                        int segments = 32;
                        int endSegment = (int) (segments * progress);

                        for (int i = 0; i < endSegment; i++) {
                                double angle1 = 2 * Math.PI * i / segments - Math.PI / 2;
                                double angle2 = 2 * Math.PI * (i + 1) / segments - Math.PI / 2;

                                int x1 = centerX + (int) (Math.cos(angle1) * radius);
                                int y1 = centerY + (int) (Math.sin(angle1) * radius);
                                int x2 = centerX + (int) (Math.cos(angle2) * radius);
                                int y2 = centerY + (int) (Math.sin(angle2) * radius);

                                RenderHelper.drawLine(context, centerX, centerY, x1, y1, color);
                                RenderHelper.drawLine(context, centerX, centerY, x2, y2, color);
                        }
                }
        }
}
