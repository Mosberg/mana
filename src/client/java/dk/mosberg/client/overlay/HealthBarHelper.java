package dk.mosberg.client.overlay;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Helper for rendering a custom health bar overlay. Only for use on the client side.
 */
public class HealthBarHelper {
    /**
     * Draws a custom health bar for the player.
     *
     * @param context The draw context
     * @param x The x position
     * @param y The y position
     * @param width The width of the bar
     * @param height The height of the bar
     * @param player The player entity
     * @param color The color of the health bar
     */
    public static void drawHealthBar(DrawContext context, int x, int y, int width, int height,
            PlayerEntity player, int color) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float percent = Math.max(0, Math.min(health / maxHealth, 1.0f));

        // Draw background
        context.fill(x, y, x + width, y + height, 0x80000000);
        // Draw border
        int borderColor = 0xFF000000;
        context.fill(x, y, x + width, y + 1, borderColor);
        context.fill(x, y + height - 1, x + width, y + height, borderColor);
        context.fill(x, y, x + 1, y + height, borderColor);
        context.fill(x + width - 1, y, x + width, y + height, borderColor);
        // Draw filled portion
        int fillWidth = (int) (width * percent);
        if (fillWidth > 0) {
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + height - 1, 0xFF000000 | color);
        }
    }
}
