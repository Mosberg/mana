package dk.mosberg.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.client.overlay.ManaHudOverlay;
import dk.mosberg.client.renderer.OverlayRenderer;
import dk.mosberg.client.renderer.RenderHelper;
import net.fabricmc.api.ClientModInitializer;

public class ManaClient implements ClientModInitializer {
	public static final String MOD_ID = "mana";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// Client-side initialization code here

		OverlayRenderer.initialize();
		RenderHelper.initialize();
		ManaHudOverlay.register();

		LOGGER.info("Mana System client initialized!");
	}
}
