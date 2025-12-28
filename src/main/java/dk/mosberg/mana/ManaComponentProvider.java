package dk.mosberg.mana;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Provider interface for retrieving ManaComponents from players. Delegates to ManaComponents
 * manager.
 */
public final class ManaComponentProvider {

    private ManaComponentProvider() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Gets the ManaComponent for a player.
     *
     * @param player The server player
     * @return The player's ManaComponent, or null if not available
     */
    @Nullable
    public static ManaComponent getManaComponent(@NotNull ServerPlayerEntity player) {
        return ManaComponents.get(player);
    }
}
