// HealthBarHelper.java - FIXED
package dk.mosberg.client.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Helper for rendering a custom health bar overlay. Only for use on the client side.
 */
public class HealthBarHelper {

    public static void initialize() {
        // Initialize any rendering resources if needed
    }

    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int BORDER_COLOR = 0xFF000000;

    /**
     * Draws a custom health bar for the player.
     *
     * @param context The draw context
     * @param x The x position
     * @param y The y position
     * @param width The width of the bar
     * @param height The height of the bar
     * @param player The player entity
     * @param color The color of the health bar (ARGB)
     */
    public static void drawHealthBar(DrawContext context, int x, int y, int width, int height,
            PlayerEntity player, int color) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float percent = maxHealth > 0 ? Math.max(0, Math.min(health / maxHealth, 1.0f)) : 0.0f;

        // Draw background
        context.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Draw border
        context.fill(x, y, x + width, y + 1, BORDER_COLOR); // Top
        context.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR); // Bottom
        context.fill(x, y, x + 1, y + height, BORDER_COLOR); // Left
        context.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR); // Right

        // Draw filled portion
        int fillWidth = (int) (width * percent);
        if (fillWidth > 1) {
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + height - 1, color);
        }
    }
}
