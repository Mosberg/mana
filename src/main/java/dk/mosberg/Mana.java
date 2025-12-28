package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.config.ManaConfig;
import dk.mosberg.mana.ManaComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Main mod class for the Mana System. Handles server-side initialization and player mana
 * management.
 */
public class Mana implements ModInitializer {
	public static final String MOD_ID = "mana";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static Mana instance;

	@Override
	public void onInitialize() {
		instance = this;

		// Initialize configuration
		ManaConfig.initialize();

		// Register server tick event for mana regeneration
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				ManaComponent component = ManaComponent.get(player);
				if (component != null) {
					component.tick();
				}
			}
		});

		LOGGER.info("Mana System initialized!");
	}

	/**
	 * Gets the mod instance.
	 *
	 * @return The Mana mod instance
	 */
	public static Mana getInstance() {
		return instance;
	}

	/**
	 * Creates a namespaced identifier for this mod.
	 *
	 * @param path The resource path
	 * @return The identifier
	 */
	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	/**
	 * Gets a player's mana pool (server-side).
	 *
	 * @param player The player
	 * @return The player's ManaComponent
	 */
	public static ManaComponent getManaComponent(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			return ManaComponent.get(serverPlayer);
		}
		return null;
	}
}
