package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.config.ManaConfig;
import dk.mosberg.mana.ManaPool;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Mana implements ModInitializer {
	public static final String MOD_ID = "mana";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Mod initialization code here
		ManaConfig.initialize();

		LOGGER.info("Mana System initialized!");
	}

	public static ClientPlayerEntity getInstance() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		return player;
	}

	public static Identifier id(String string) {
		return Identifier.of(MOD_ID, string);
	}

	public ManaPool getManaPool(PlayerEntity player) {
		// Placeholder implementation
		return new ManaPool();
	}
}
