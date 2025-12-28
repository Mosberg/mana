// ManaComponents.java - IMPROVED
package dk.mosberg.mana;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Manager for attaching ManaComponent to players. Uses UUID-based storage for proper persistence
 * with thread-safe concurrent access.
 */
public final class ManaComponents {

    private static final Map<UUID, ManaComponent> MANA_COMPONENTS = new ConcurrentHashMap<>();

    private ManaComponents() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Gets or creates a ManaComponent for a player.
     *
     * @param player The player
     * @return The player's ManaComponent
     */
    @NotNull
    public static ManaComponent get(@NotNull ServerPlayerEntity player) {
        return MANA_COMPONENTS.computeIfAbsent(player.getUuid(), uuid -> new ManaComponent(player));
    }

    /**
     * Gets a ManaComponent if it exists, without creating a new one.
     *
     * @param player The player
     * @return The player's ManaComponent, or null if not yet created
     */
    @Nullable
    public static ManaComponent getIfExists(@NotNull ServerPlayerEntity player) {
        return MANA_COMPONENTS.get(player.getUuid());
    }

    /**
     * Checks if a player has a ManaComponent.
     *
     * @param player The player
     * @return true if component exists, false otherwise
     */
    public static boolean has(@NotNull ServerPlayerEntity player) {
        return MANA_COMPONENTS.containsKey(player.getUuid());
    }

    /**
     * Removes a player's ManaComponent. Should be called when player disconnects.
     *
     * @param player The player
     */
    public static void remove(@NotNull ServerPlayerEntity player) {
        MANA_COMPONENTS.remove(player.getUuid());
    }

    /**
     * Clears all stored components. Should be called on server shutdown.
     */
    public static void clear() {
        MANA_COMPONENTS.clear();
    }

    /**
     * Gets the number of active mana components.
     *
     * @return The component count
     */
    public static int size() {
        return MANA_COMPONENTS.size();
    }
}
