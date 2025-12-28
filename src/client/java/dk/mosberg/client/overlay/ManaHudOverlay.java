package dk.mosberg.client.overlay;

import dk.mosberg.Mana;
import dk.mosberg.config.ManaConfig;
import dk.mosberg.mana.ManaPool;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Renders the three mana pools above the health bar
 */
public class ManaHudOverlay {

    // Overlay customization (can be made configurable)

    // Mana bar config
    private static int BAR_WIDTH = 81;
    private static int BAR_HEIGHT = 5;
    private static int BAR_SPACING = 2;
    private static int PRIMARY_COLOR = 0x00AAFF; // Blue
    private static int SECONDARY_COLOR = 0x00FF00; // Green
    private static int TERTIARY_COLOR = 0xFF00FF; // Purple

    // Health bar config
    private static int HEALTH_BAR_WIDTH = 81;
    private static int HEALTH_BAR_HEIGHT = 6;
    private static int HEALTH_BAR_COLOR = 0xFF5555; // Red
    private static int HEALTH_BAR_OFFSET = 10; // px above mana bars

    // Status icon config
    private static int STATUS_ICON_SIZE = 10;
    private static int STATUS_ICON_SPACING = 2;
    private static int STATUS_ICON_OFFSET = 12; // px above health bar

    /**
     * Allows runtime customization of overlay appearance.
     */

    /**
     * Allows runtime customization of overlay appearance.
     */
    public static void setOverlayStyle(int width, int height, int spacing, int primaryColor,
            int secondaryColor, int tertiaryColor, int healthBarWidth, int healthBarHeight,
            int healthBarColor) {
        BAR_WIDTH = width;
        BAR_HEIGHT = height;
        BAR_SPACING = spacing;
        PRIMARY_COLOR = primaryColor;
        SECONDARY_COLOR = secondaryColor;
        TERTIARY_COLOR = tertiaryColor;
        HEALTH_BAR_WIDTH = healthBarWidth;
        HEALTH_BAR_HEIGHT = healthBarHeight;
        HEALTH_BAR_COLOR = healthBarColor;
    }

    public static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!ManaConfig.isOverlayEnabled())
            return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) {
            return;
        }

        PlayerEntity player = client.player;
        ManaPool manaPool = ((Mana) Mana.getInstance()).getManaPool(player);

        int screenWidth = drawContext.getScaledWindowWidth();
        int screenHeight = drawContext.getScaledWindowHeight();

        // Configurable scale and offset
        double scale = ManaConfig.getOverlayScale();
        int xOffset = ManaConfig.getOverlayXOffset();
        int yOffset = ManaConfig.getOverlayYOffset();
        double alpha = ManaConfig.getOverlayTransparency();

        // Mana bars position (bottom center, scaled and offset)
        int manaX = (int) (screenWidth / 2 - BAR_WIDTH * scale / 2) + xOffset;
        int manaY = (int) (screenHeight - 49 - (BAR_HEIGHT + BAR_SPACING) * 3 * scale) + yOffset;

        // Health bar position (above mana bars)
        int healthX = (int) (screenWidth / 2 - HEALTH_BAR_WIDTH * scale / 2) + xOffset;
        int healthY = (int) (manaY - HEALTH_BAR_OFFSET * scale);

        // Status icons position (above health bar)
        int statusX = (int) (screenWidth / 2 - (STATUS_ICON_SIZE * 5 * scale) / 2) + xOffset;
        int statusY = (int) (healthY - STATUS_ICON_OFFSET * scale);

        // Set alpha for overlay
        int alphaInt = (int) (255 * Math.max(0.0, Math.min(1.0, alpha)));

        // Draw status effect icons
        StatusIconHelper.drawStatusIcons(drawContext, statusX, statusY, player,
                (int) (STATUS_ICON_SIZE * scale), (int) (STATUS_ICON_SPACING * scale));

        // Draw custom health bar
        HealthBarHelper.drawHealthBar(drawContext, healthX, healthY,
                (int) (HEALTH_BAR_WIDTH * scale), (int) (HEALTH_BAR_HEIGHT * scale), player,
                (HEALTH_BAR_COLOR & 0x00FFFFFF) | (alphaInt << 24));

        // Draw mana bars
        drawManaBar(drawContext, manaX, manaY, manaPool.getPrimaryPercent(),
                (PRIMARY_COLOR & 0x00FFFFFF) | (alphaInt << 24));
        drawManaBar(drawContext, manaX, manaY + (int) ((BAR_HEIGHT + BAR_SPACING) * scale),
                manaPool.getSecondaryPercent(), (SECONDARY_COLOR & 0x00FFFFFF) | (alphaInt << 24));
        drawManaBar(drawContext, manaX, manaY + (int) ((BAR_HEIGHT + BAR_SPACING) * 2 * scale),
                manaPool.getTertiaryPercent(), (TERTIARY_COLOR & 0x00FFFFFF) | (alphaInt << 24));
    }

    private static void drawManaBar(DrawContext context, int x, int y, double percent, int color) {
        // Draw background (dark)
        context.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0x80000000);

        // Draw border manually
        int borderColor = 0xFF000000;
        // Top
        context.fill(x, y, x + BAR_WIDTH, y + 1, borderColor);
        // Bottom
        context.fill(x, y + BAR_HEIGHT - 1, x + BAR_WIDTH, y + BAR_HEIGHT, borderColor);
        // Left
        context.fill(x, y, x + 1, y + BAR_HEIGHT, borderColor);
        // Right
        context.fill(x + BAR_WIDTH - 1, y, x + BAR_WIDTH, y + BAR_HEIGHT, borderColor);

        // Draw filled portion
        int fillWidth = (int) (BAR_WIDTH * percent);
        if (fillWidth > 0) {
            context.fill(x + 1, y + 1, x + fillWidth - 1, y + BAR_HEIGHT - 1, 0xFF000000 | color);
        }

        // Draw shine effect
        if (percent > 0) {
            int shineWidth = (int) (fillWidth * 0.3);
            int shineColor = 0x40FFFFFF;
            context.fill(x + 1, y + 1, x + shineWidth, y + 2, shineColor);
        }
    }

    public static void register() {
        HudElementRegistry.addLast(Mana.id("mana_hud_overlay"),
                (HudElement) ManaHudOverlay::onHudRender);
    }
}
