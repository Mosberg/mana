package dk.mosberg.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dk.mosberg.mana.ManaPool;
import dk.mosberg.mana.ManaPool.ManaPoolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Utility methods for working with ManaPool instances. Provides convenient access patterns and
 * common operations for mana management.
 */
public final class ManaPoolHelper {

    private ManaPoolHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Checks if a player has enough mana available.
     *
     * @param player The player to check
     * @param amount The amount of mana required
     * @return true if player has enough mana, false otherwise
     */
    public static boolean hasEnoughMana(@Nullable PlayerEntity player, double amount) {
        if (player == null || amount < 0) {
            return false;
        }

        ManaPool pool = getManaPool(player);
        return pool != null && pool.getTotalMana() >= amount;
    }

    /**
     * Attempts to consume mana from a player's pool.
     *
     * @param player The player
     * @param amount The amount to consume
     * @return true if mana was consumed, false otherwise
     */
    public static boolean tryConsumeMana(@Nullable PlayerEntity player, double amount) {
        if (player == null || amount < 0) {
            return false;
        }

        ManaPool pool = getManaPool(player);
        return pool != null && pool.consumeMana(amount);
    }

    /**
     * Restores mana to a player's pool.
     *
     * @param player The player
     * @param amount The amount to restore
     */
    public static void restoreMana(@Nullable PlayerEntity player, double amount) {
        if (player == null || amount <= 0) {
            return;
        }

        ManaPool pool = getManaPool(player);
        if (pool != null) {
            pool.restoreMana(amount);
        }
    }

    /**
     * Gets the total mana percentage across all pools.
     *
     * @param player The player
     * @return The total mana percentage (0.0-1.0), or 0.0 if unavailable
     */
    public static double getTotalManaPercent(@Nullable PlayerEntity player) {
        if (player == null) {
            return 0.0;
        }

        ManaPool pool = getManaPool(player);
        if (pool == null) {
            return 0.0;
        }

        double total = pool.getTotalMana();
        double max = pool.getTotalMaxMana();
        return max > 0 ? total / max : 0.0;
    }

    /**
     * Gets a specific pool's mana percentage.
     *
     * @param player The player
     * @param type The pool type
     * @return The pool's mana percentage (0.0-1.0), or 0.0 if unavailable
     */
    public static double getPoolPercent(@Nullable PlayerEntity player, @NotNull ManaPoolType type) {
        if (player == null) {
            return 0.0;
        }

        ManaPool pool = getManaPool(player);
        if (pool == null) {
            return 0.0;
        }

        return switch (type) {
            case PRIMARY -> pool.getPrimaryPercent();
            case SECONDARY -> pool.getSecondaryPercent();
            case TERTIARY -> pool.getTertiaryPercent();
        };
    }

    /**
     * Enables or disables mana regeneration for a player.
     *
     * @param player The player
     * @param regenerating Whether mana should regenerate
     */
    public static void setRegenerating(@Nullable PlayerEntity player, boolean regenerating) {
        if (player == null) {
            return;
        }

        ManaPool pool = getManaPool(player);
        if (pool != null) {
            pool.setRegenerating(regenerating);
        }
    }

    /**
     * Gets the ManaPool for a player (helper method).
     *
     * @param player The player
     * @return The player's ManaPool, or null if unavailable
     */
    @Nullable
    private static ManaPool getManaPool(@NotNull PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            var component = dk.mosberg.Mana.getManaComponent(serverPlayer);
            return component != null ? component.getManaPool() : null;
        }
        return null;
    }

    /**
     * Formats mana display string (e.g., "120 / 250").
     *
     * @param player The player
     * @param type The pool type to format
     * @return Formatted string, or empty string if unavailable
     */
    @NotNull
    public static String formatMana(@Nullable PlayerEntity player, @NotNull ManaPoolType type) {
        if (player == null) {
            return "";
        }

        ManaPool pool = getManaPool(player);
        if (pool == null) {
            return "";
        }

        return switch (type) {
            case PRIMARY -> String.format("%.0f / %.0f", pool.getPrimaryMana(),
                    pool.getPrimaryMax());
            case SECONDARY -> String.format("%.0f / %.0f", pool.getSecondaryMana(),
                    pool.getSecondaryMax());
            case TERTIARY -> String.format("%.0f / %.0f", pool.getTertiaryMana(),
                    pool.getTertiaryMax());
        };
    }
}
