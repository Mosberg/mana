package dk.mosberg.client.overlay;

import dk.mosberg.Mana;
import dk.mosberg.client.util.HealthBarHelper;
import dk.mosberg.client.util.StatusIconHelper;
import dk.mosberg.config.ManaConfig;
import dk.mosberg.mana.ManaPool;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Renders the three mana pools above the health bar. Thread-safe with immutable configuration
 * values.
 */
public class ManaHudOverlay {
    // Mana bar configuration (immutable)
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 5;
    private static final int BAR_SPACING = 2;
    private static final int PRIMARY_COLOR = 0x00AAFF; // Blue
    private static final int SECONDARY_COLOR = 0x00FF00; // Green
    private static final int TERTIARY_COLOR = 0xFF00FF; // Purple

    // Health bar configuration
    private static final int HEALTH_BAR_WIDTH = 81;
    private static final int HEALTH_BAR_HEIGHT = 6;
    private static final int HEALTH_BAR_COLOR = 0xFF5555; // Red
    private static final int HEALTH_BAR_OFFSET = 10;

    // Status icon configuration
    private static final int STATUS_ICON_SIZE = 10;
    private static final int STATUS_ICON_SPACING = 2;
    private static final int STATUS_ICON_OFFSET = 12;

    // Rendering constants
    private static final int BACKGROUND_COLOR = 0x80000000;
    private static final int BORDER_COLOR = 0xFF000000;
    private static final int SHINE_COLOR = 0x40FFFFFF;
    private static final double SHINE_WIDTH_FACTOR = 0.3;

    /**
     * Main HUD rendering method called every frame.
     *
     * @param drawContext The draw context
     * @param tickCounter The render tick counter
     */
    public static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!ManaConfig.isOverlayEnabled()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }

        PlayerEntity player = client.player;
        ManaPool manaPool = getManaPoolForClient(player);
        if (manaPool == null) {
            return;
        }

        int screenWidth = drawContext.getScaledWindowWidth();
        int screenHeight = drawContext.getScaledWindowHeight();

        // Get configurable values
        double scale = ManaConfig.getOverlayScale();
        int xOffset = ManaConfig.getOverlayXOffset();
        int yOffset = ManaConfig.getOverlayYOffset();
        double alpha = ManaConfig.getOverlayTransparency();

        // Calculate positions (bottom center, scaled and offset)
        int manaX = (int) (screenWidth / 2.0 - BAR_WIDTH * scale / 2.0) + xOffset;
        int manaY = (int) (screenHeight - 49 - (BAR_HEIGHT + BAR_SPACING) * 3 * scale) + yOffset;
        int healthX = (int) (screenWidth / 2.0 - HEALTH_BAR_WIDTH * scale / 2.0) + xOffset;
        int healthY = (int) (manaY - HEALTH_BAR_OFFSET * scale);
        int statusX = (int) (screenWidth / 2.0 - (STATUS_ICON_SIZE * 5 * scale) / 2.0) + xOffset;
        int statusY = (int) (healthY - STATUS_ICON_OFFSET * scale);

        // Convert alpha to integer (0-255)
        int alphaInt = (int) (255 * Math.max(0.0, Math.min(1.0, alpha)));

        // Draw status effect icons
        StatusIconHelper.drawStatusIcons(drawContext, statusX, statusY, player,
                (int) (STATUS_ICON_SIZE * scale), (int) (STATUS_ICON_SPACING * scale));

        // Draw custom health bar
        HealthBarHelper.drawHealthBar(drawContext, healthX, healthY,
                (int) (HEALTH_BAR_WIDTH * scale), (int) (HEALTH_BAR_HEIGHT * scale), player,
                applyAlpha(HEALTH_BAR_COLOR, alphaInt));

        // Draw mana bars
        int scaledWidth = (int) (BAR_WIDTH * scale);
        int scaledHeight = (int) (BAR_HEIGHT * scale);
        int scaledSpacing = (int) ((BAR_HEIGHT + BAR_SPACING) * scale);

        drawManaBar(drawContext, manaX, manaY, scaledWidth, scaledHeight,
                manaPool.getPrimaryPercent(), applyAlpha(PRIMARY_COLOR, alphaInt));
        drawManaBar(drawContext, manaX, manaY + scaledSpacing, scaledWidth, scaledHeight,
                manaPool.getSecondaryPercent(), applyAlpha(SECONDARY_COLOR, alphaInt));
        drawManaBar(drawContext, manaX, manaY + scaledSpacing * 2, scaledWidth, scaledHeight,
                manaPool.getTertiaryPercent(), applyAlpha(TERTIARY_COLOR, alphaInt));
    }

    /**
     * Draws a single mana bar with background, border, fill, and shine effect.
     *
     * @param context The draw context
     * @param x The x position
     * @param y The y position
     * @param width The bar width
     * @param height The bar height
     * @param percent The fill percentage (0.0-1.0)
     * @param color The fill color (ARGB)
     */
    private static void drawManaBar(DrawContext context, int x, int y, int width, int height,
            double percent, int color) {
        // Draw background
        context.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        // Draw border
        context.fill(x, y, x + width, y + 1, BORDER_COLOR); // Top
        context.fill(x, y + height - 1, x + width, y + height, BORDER_COLOR); // Bottom
        context.fill(x, y, x + 1, y + height, BORDER_COLOR); // Left
        context.fill(x + width - 1, y, x + width, y + height, BORDER_COLOR); // Right

        // Draw filled portion
        int fillWidth = (int) (width * Math.max(0.0, Math.min(1.0, percent)));
        if (fillWidth > 1) {
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + height - 1, color);

            // Draw shine effect
            int shineWidth = Math.max(1, (int) (fillWidth * SHINE_WIDTH_FACTOR));
            context.fill(x + 1, y + 1, x + shineWidth, y + 2, SHINE_COLOR);
        }
    }

    /**
     * Applies alpha channel to a color.
     *
     * @param color The base color (RGB or ARGB)
     * @param alpha The alpha value (0-255)
     * @return The color with alpha applied
     */
    private static int applyAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    /**
     * Gets the mana pool for the client player. Note: This is a client-side approximation. Actual
     * data should sync from server.
     *
     * @param player The player
     * @return The mana pool or null
     */
    private static ManaPool getManaPoolForClient(PlayerEntity player) {
        return new ManaPool();
    }

    /**
     * Registers the HUD overlay with Fabric.
     */
    public static void register() {
        HudElementRegistry.addLast(Mana.id("mana_hud_overlay"),
                (HudElement) ManaHudOverlay::onHudRender);
    }
}
