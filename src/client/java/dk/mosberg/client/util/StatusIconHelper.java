// StatusIconHelper.java - Already correct, no changes needed
package dk.mosberg.client.util;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

/**
 * Helper for rendering status effect icons on the HUD overlay. Only for use on the client side.
 */
public class StatusIconHelper {

    public static void initialize() {
        // Initialize any rendering resources if needed
    }

    /**
     * Draws status effect icons for all active effects on the player.
     *
     * @param context The draw context
     * @param x The x position (left of first icon)
     * @param y The y position (top of icons)
     * @param player The player entity
     * @param iconSize The size of each icon (width/height)
     * @param spacing The space between icons
     */
    public static void drawStatusIcons(DrawContext context, int x, int y, PlayerEntity player,
            int iconSize, int spacing) {
        List<StatusEffectInstance> effects = player.getStatusEffects().stream()
                .filter(StatusEffectInstance::shouldShowIcon).collect(Collectors.toList());

        if (effects.isEmpty()) {
            return;
        }

        int index = 0;
        for (StatusEffectInstance effect : effects) {
            // Get the icon texture for the status effect
            Identifier icon = InGameHud.getEffectTexture(effect.getEffectType());

            if (icon != null) {
                // Draw the icon using drawTexture
                context.drawTexture(RenderPipelines.GUI_TEXTURED, icon,
                        x + index * (iconSize + spacing), y, 0.0f, 0.0f, iconSize, iconSize,
                        iconSize, iconSize);
            } else {
                // Fallback: draw a potion bottle
                ItemStack stack = new ItemStack(Items.POTION);
                context.drawItem(stack, x + index * (iconSize + spacing), y);
            }

            index++;
        }
    }
}
